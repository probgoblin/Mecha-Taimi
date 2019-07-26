package me.cbitler.raidbot.raids;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.database.Database;
import me.cbitler.raidbot.database.QueryResult;
import me.cbitler.raidbot.utility.Reactions;
import me.cbitler.raidbot.utility.RoleTemplates;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Serves as a manager for all of the raids. This includes creating, loading, and deleting raids
 * @author Christopher Bitler
 * @author Franziska Mueller
 */
public class RaidManager {

    static List<Raid> raids = new ArrayList<>();

    /**
     * Create a raid. This turns a PendingRaid object into a Raid object and inserts it into the list of raids.
     * It also sends the associated embedded message and adds the reactions for people to join to the embed
     * @param raid The pending raid to create
     */
    public static void createRaid(PendingRaid raid) {
        MessageEmbed message = raid.isDisplayShort() ? buildEmbedShort(raid) : buildEmbed(raid);

        Guild guild = RaidBot.getInstance().getServer(raid.getServerId());
        List<TextChannel> channels = guild.getTextChannelsByName(raid.getAnnouncementChannel(), true);
        if(channels.size() > 0) {
            // We always go with the first channel if there is more than one
            try {
                channels.get(0).sendMessage(message).queue(message1 -> {
                    boolean inserted = insertToDatabase(raid, message1.getId(), message1.getGuild().getId(), message1.getChannel().getId());
                    if (inserted) {
                        Raid newRaid = new Raid(message1.getId(), message1.getGuild().getId(), message1.getChannel().getId(), raid.getLeaderName(), raid.getName(), raid.getDescription(), raid.getDate(), raid.getTime(), raid.isOpenWorld(), raid.isDisplayShort());
                        newRaid.roles.addAll(raid.rolesWithNumbers);
                        raids.add(newRaid);
                        newRaid.updateMessage();

                        List<Emote> emoteList;
                        if (newRaid.isOpenWorld)
                            emoteList = Reactions.getOpenWorldEmotes();
                        else
                            emoteList = Reactions.getCoreClassEmotes();
                        
                        try {
                        	for (Emote emote : emoteList)
                        		message1.addReaction(emote).queue();
                        } catch (Exception excp) {
                        	System.out.println("Could not add all reactions to the event message. At least one emoji was null.");
                        }
                    } else {
                        message1.delete().queue();
                    }
                });
            } catch (Exception e) {
                System.out.println("Error encountered in sending message.");
                e.printStackTrace();
                throw e;
            }
        }
    }
    
    /**
     * Create a fractal event
     * @param name
     * @param date
     * @param time
     * @param teamCompId
     */
    public static void createFractal(User author, String serverId, String name, String date, String time, int teamCompId) {
		// TODO Auto-generated method stub
		PendingRaid fractalEvent = new PendingRaid();
		fractalEvent.setLeaderName(author.getName());
        fractalEvent.setServerId(serverId);		
        fractalEvent.setName(name);
		fractalEvent.setDescription("-");
		fractalEvent.setDate(date);
		fractalEvent.setTime(time);
		fractalEvent.setDisplayShort(true);
		fractalEvent.addTemplateRoles(RoleTemplates.getFractalTemplates()[teamCompId]);
		
		String fractalChannel = RaidBot.getInstance().getFractalChannel(serverId);
		if (RaidBot.getInstance().checkChannel(fractalEvent.getServerId(), fractalChannel)) {
			fractalEvent.setAnnouncementChannel(fractalChannel);
		} else {
			author.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("The specified fractal channel is invalid. It needs to be set with !setFractalChannel [channelname without hash] by someone with MANAGE SERVER permissions.").queue());
			return;
		}
		createRaid(fractalEvent);
	}
      
    /**
     * Insert a raid into the database
     * @param raid The raid to insert
     * @param messageId The embedded message / 'raidId'
     * @param serverId The serverId related to this raid
     * @param channelId The channelId for the announcement of this raid
     * @return True if inserted, false otherwise
     */
    private static boolean insertToDatabase(PendingRaid raid, String messageId, String serverId, String channelId) {
        RaidBot bot = RaidBot.getInstance();
        Database db = bot.getDatabase();

        String roles = formatRolesForDatabase(raid.getRolesWithNumbers());

        try {
            db.update("INSERT INTO `raids` (`raidId`, `serverId`, `channelId`, `isDisplayShort`, `isOpenWorld`, `leader`, `name`, `description`, `date`, `time`, `roles`) VALUES (?,?,?,?,?,?,?,?,?,?,?)", new String[] {
                    messageId,
                    serverId,
                    channelId,
                    Boolean.toString(raid.isDisplayShort()),
                    Boolean.toString(raid.isOpenWorld()),
                    raid.getLeaderName(),
                    raid.getName(),
                    raid.getDescription(),
                    raid.getDate(),
                    raid.getTime(),
                    roles
            });
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Load raids
     * This first queries all of the raids and loads the raid data and adds the raids to the raid list
     * Then, it queries the raid users and inserts them into their relevant raids, updating the embedded messages
     * Finally, it queries the raid users' flex roles and inserts those to the raids
     */
    public static void loadRaids() {
        RaidBot bot = RaidBot.getInstance();
        Database db = bot.getDatabase();

        try {
            QueryResult results = db.query("SELECT * FROM `raids`", new String[] {});
            while (results.getResults().next()) {
                String name = results.getResults().getString("name");
                String description = results.getResults().getString("description");
                if(description == null) {
                    description = "N/A";
                }
                String date = results.getResults().getString("date");
                String time = results.getResults().getString("time");
                String rolesText = results.getResults().getString("roles");
                String messageId = results.getResults().getString("raidId");
                String serverId = results.getResults().getString("serverId");
                String channelId = results.getResults().getString("channelId");

                String leaderName = null;
                try {
                    leaderName = results.getResults().getString("leader");
                } catch (Exception e) { }

                boolean isOpenWorld = false;
                try {
                    isOpenWorld = results.getResults().getString("isOpenWorld").equals("true");
                } catch (Exception e) { }
                
                boolean isDisplayShort = false;
                try {
                	isDisplayShort = results.getResults().getString("isDisplayShort").equals("true");
                } catch (Exception e) { }

                Raid raid = new Raid(messageId, serverId, channelId, leaderName, name, description, date, time, isOpenWorld, isDisplayShort);
                String[] roleSplit = rolesText.split(";");
                for(String roleAndAmount : roleSplit) {
                    String[] parts = roleAndAmount.split(":");
                    try {
                    	int amnt = Integer.parseInt(parts[0]);
                    	String role = parts[1];
                        raid.roles.add(new RaidRole(amnt, role));
                    } catch (Exception excp) {
                    	System.out.println("Invalid format for role with amount: " + roleAndAmount);
                    }                    
                }
                if (raid.roles.size() > 0) // this should always be the case
                	raids.add(raid);
                else {
                	// delete this raid from the database
                	try {
                		db.update("DELETE FROM `raids` WHERE `raidId` = ?", new String[]{ messageId });
                		db.update("DELETE FROM `raidUsers` WHERE `raidId` = ?", new String[]{ messageId });
                        db.update("DELETE FROM `raidUsersFlexRoles` WHERE `raidId` = ?", new String[]{messageId});
                	} catch (Exception excp) {
                		System.out.println("Could not delete raid without roles from database.");
                	}
                }
            }
            results.getResults().close();
            results.getStmt().close();

            QueryResult userResults = db.query("SELECT * FROM `raidUsers`", new String[] {});

            while(userResults.getResults().next()) {
                String id = userResults.getResults().getString("userId");
                String name = userResults.getResults().getString("username");
                String spec = userResults.getResults().getString("spec");
                String role = userResults.getResults().getString("role");
                String raidId = userResults.getResults().getString("raidId");

                Raid raid = RaidManager.getRaid(raidId);
                if(raid != null) {
                    raid.addUser(id, name, spec, role, false, false);
                }
            }

            QueryResult userFlexRolesResults = db.query("SELECT * FROM `raidUsersFlexroles`", new String[] {});

            while(userFlexRolesResults.getResults().next()) {
                String id = userFlexRolesResults.getResults().getString("userId");
                String name = userFlexRolesResults.getResults().getString("username");
                String spec = userFlexRolesResults.getResults().getString("spec");
                String role = userFlexRolesResults.getResults().getString("role");
                String raidId = userFlexRolesResults.getResults().getString("raidId");

                Raid raid = RaidManager.getRaid(raidId);
                if(raid != null) {
                    raid.addUserFlexRole(id, name, spec, role, false, false);
                }
            }

            for(Raid raid : raids) {
                raid.updateMessage();
            }
        } catch (SQLException e) {
            System.out.println("Couldn't load events... exiting.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Delete the raid from the database and maps, and delete the message if it is still there
     * @param messageId The raid ID
     * @return true if deleted, false if not deleted
     */
    public static boolean deleteRaid(String messageId) {
        Raid r = getRaid(messageId);
        if (r != null) {
            //try {
            //    RaidBot.getInstance().getServer(r.getServerId())
            //            .getTextChannelById(r.getChannelId()).getMessageById(messageId).queue(message -> message.delete().queue());
            //} catch (Exception e) {
            //    // Nothing, the message doesn't exist - it can happen
            //}

            Iterator<Raid> raidIterator = raids.iterator();
            while (raidIterator.hasNext()) {
                Raid raid = raidIterator.next();
                if (raid.getMessageId().equalsIgnoreCase(messageId)) {
                    raidIterator.remove();
                }
            }

            try {
                RaidBot.getInstance().getDatabase().update("DELETE FROM `raids` WHERE `raidId` = ?", new String[]{
                        messageId
                });
                RaidBot.getInstance().getDatabase().update("DELETE FROM `raidUsers` WHERE `raidId` = ?", new String[]{
                        messageId
                });
                RaidBot.getInstance().getDatabase().update("DELETE FROM `raidUsersFlexRoles` WHERE `raidId` = ?",
                        new String[]{messageId});
            } catch (Exception e) {
                System.out.println("Error encountered deleting event.");
            }

            return true;
        }

        return false;
    }

    /**
     * Get a raid from the discord message ID
     * @param messageId The discord message ID associated with the raid's embedded message
     * @return The raid object related to that messageId, if it exist.
     */
    public static Raid getRaid(String messageId)
    {
        for(Raid raid : raids) {
            if (raid.messageId.equalsIgnoreCase(messageId)) {
                return raid;
            }
        }
        return null;
    }
    
    /**
     * Get all raids
     * @return The list of all raid objects.
     */
    public static List<Raid> getAllRaids()
    {
    	return raids;
    }

    /**
     * Formats the roles associated with a raid in a form that can be inserted into a database row.
     * This combines them as [number]:[name];[number]:[name];...
     * @param rolesWithNumbers The roles and their amounts
     * @return The formatted string
     */
    public static String formatRolesForDatabase(List<RaidRole> rolesWithNumbers) {
        String data = "";

        for (int i = 0; i < rolesWithNumbers.size(); i++) {
            RaidRole role = rolesWithNumbers.get(i);
            String roleName = role.name;
            if(role.isFlexOnly()) roleName = "!"+roleName;
            if(i == rolesWithNumbers.size() - 1) {
                data += (role.amount + ":" + roleName);
            } else {
                data += (role.amount + ":" + roleName + ";");
            }
        }

        return data;
    }

    /**
     * Create a message embed to show the raid
     * @param raid The raid object
     * @return The embedded message
     */
    private static MessageEmbed buildEmbed(PendingRaid raid) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(raid.getName());
        builder.addField("Description:" , raid.getDescription(), false);
        builder.addBlankField(false);
        if (raid.getLeaderName() != null) {
            builder.addField("Leader: ", "**" + raid.getLeaderName() + "**", false);
        }
        builder.addBlankField(false);
        builder.addField("Date: ", raid.getDate(), true);
        builder.addField("Time: ", raid.getTime(), true);
        builder.addBlankField(false);
        return builder.build();
    }
    
    /**
     * Create a short message embed to show the raid
     * @param raid The raid object
     * @return The embedded message
     */
    private static MessageEmbed buildEmbedShort(PendingRaid raid) {
    	EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(raid.getName() + " - [" + raid.getDate() + " " + raid.getTime() + "]");

        return builder.build();
    }

//    /**
//     * Builds the text to go into the roles field in the embedded message
//     * @param raid The raid object
//     * @return The role text
//     */
//    private static String buildRolesText(PendingRaid raid) {
//        String text = "";
//        for(RaidRole role : raid.getRolesWithNumbers()) {
//            if(role.isFlexOnly()) continue;
//            text += ("**" + role.name + ":**\n");
//        }
//        return text;
//    }
//
//    /**
//     * Build the flex role text. This is blank here as we have no flex roles at this point.
//     * @param raid
//     * @return The flex roles text (blank here)
//     */
//    private static String buildFlexRolesText(PendingRaid raid) {
//        String text = "";
//        for(RaidRole role : raid.getRolesWithNumbers()) {
//            if(role.isFlexOnly()) text += ("**" + role.name + " (" + role.amount + "):**\n");
//        }
//        return text;
//    }
}
