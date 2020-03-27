package me.cbitler.raidbot;

import me.cbitler.raidbot.commands.*;
import me.cbitler.raidbot.creation.CreationStep;
import me.cbitler.raidbot.edit.EditStep;
import me.cbitler.raidbot.database.Database;
import me.cbitler.raidbot.database.QueryResult;
import me.cbitler.raidbot.deselection.DeselectionStep;
import me.cbitler.raidbot.handlers.ChannelMessageHandler;
import me.cbitler.raidbot.handlers.DMHandler;
import me.cbitler.raidbot.handlers.ReactionHandler;
import me.cbitler.raidbot.raids.PendingRaid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.selection.SelectionStep;
import me.cbitler.raidbot.swap.SwapStep;
import me.cbitler.raidbot.utility.GuildCountUtil;
import me.cbitler.raidbot.utility.AutomatedTaskExecutor;
import me.cbitler.raidbot.utility.EventCreator;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class representing the raid bot itself.
 * This stores the creation/roleSelection map data and also the list of pendingRaids
 * Additionally, it also stores the database in use by the bot and serves as a way
 * for other classes to access it.
 *
 * @author Christopher Bitler
 * @author Franziska Mueller
 */
public class RaidBot {
    private static RaidBot instance;
    private JDA jda;
    
    private enum ChannelType { ARCHIVE, FRACTALS, AUTOEVENTS };

    HashMap<String, CreationStep> creation = new HashMap<String, CreationStep>();
    HashMap<String, EditStep> edits = new HashMap<String, EditStep>();
    HashMap<String, PendingRaid> pendingRaids = new HashMap<String, PendingRaid>();
    HashMap<String, SelectionStep> roleSelection = new HashMap<String, SelectionStep>();
    HashMap<String, DeselectionStep> roleDeselection = new HashMap<String, DeselectionStep>();
    HashMap<String, SwapStep> roleSwap = new HashMap<String, SwapStep>();

    Set<String> editList = new HashSet<String>();

    //TODO: This should be moved to it's own settings thing
    HashMap<String, String> raidLeaderRoleCache = new HashMap<>();
    HashMap<String, String> fractalCreatorRoleCache = new HashMap<>();
    HashMap<String, String> fractalChannelCache = new HashMap<>();
    HashMap<String, String> archiveChannelCache = new HashMap<>();

    Database db;
    
    AutomatedTaskExecutor autoEventCreator;

    /**
     * Create a new instance of the raid bot with the specified JDA api
     * @param jda The API for the bot to use
     */
    public RaidBot(JDA jda) {
        instance = this;

        this.jda = jda;
        jda.addEventListener(new DMHandler(this), new ChannelMessageHandler(), new ReactionHandler());
        db = new Database("events.db");
        db.connect();
        RaidManager.loadRaids();

        CommandRegistry.addCommand("help", new HelpCommand());
        CommandRegistry.addCommand("info", new InfoCommand());
        CommandRegistry.addCommand("endEvent", new EndRaidCommand());
        CommandRegistry.addCommand("endAllEvents", new EndAllCommand());
        
        autoEventCreator = new AutomatedTaskExecutor(new EventCreator());
        autoEventCreator.startExecutionAt(0, 0, 0);

        new Thread(() -> {
            while (true) {
                try {
                    GuildCountUtil.sendGuilds(jda);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000*60*5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Map of UserId -> creation step for people in the creation process
     * @return The map of UserId -> creation step for people in the creation process
     */
    public HashMap<String, CreationStep> getCreationMap() {
        return creation;
    }

    /**
     * Map of UserId -> edit step for raids in the edit process
     * @return The map of UserId -> edit step for raids in the edit process
     */
    public HashMap<String, EditStep> getEditMap() {
        return edits;
    }

    /**
     * Map of the UserId -> roleSelection step for people in the role selection process
     * @return The map of the UserId -> roleSelection step for people in the role selection process
     */
    public HashMap<String, SelectionStep> getRoleSelectionMap() {
        return roleSelection;
    }

    /**
     * Map of the UserId -> roleDeselection step for people in the role deselection process
     * @return The map of the UserId -> roleDeselection step for people in the role deselection process
     */
    public HashMap<String, DeselectionStep> getRoleDeselectionMap() {
        return roleDeselection;
    }
    
    /**
     * Map of the UserId -> roleSwap step for people in the role swapping process
     * @return The map of the UserId -> roleSwap step for people in the role swapping process
     */
    public HashMap<String, SwapStep> getRoleSwapMap() {
        return roleSwap;
    }

    /**
     * Map of the UserId -> pendingRaid step for raids in the setup process
     * @return The map of UserId -> pendingRaid
     */
    public HashMap<String, PendingRaid> getPendingRaids() {
        return pendingRaids;
    }

    /**
     * List of messageIDs for raids in the edit process
     * @return List of messageIDs for raids in the edit process
     */
    public Set<String> getEditList() {
        return editList;
    }

    /**
     * Get the JDA server object related to the server ID
     * @param id The server ID
     * @return The server related to that that ID
     */
    public Guild getServer(String id) {
        return jda.getGuildById(id);
    }

    /**
     * Exposes the underlying library. This is mainly necessary for getting Emojis
     * @return The JDA library object
     */
    public JDA getJda() {
        return jda;
    }

    /**
     * Get the database that the bot is using
     * @return The database that the bot is using
     */
    public Database getDatabase() {
        return db;
    }

    /**
     * Get the raid leader role for a specific server.
     * This works by caching the role once it's retrieved once, and returning the default if a server hasn't set one.
     * @param serverId the ID of the server
     * @return The name of the role that is considered the raid leader for that server
     */
    public String getRaidLeaderRole(String serverId) {
        if (raidLeaderRoleCache.get(serverId) != null) {
            return raidLeaderRoleCache.get(serverId);
        } else {
            try {
                QueryResult results = db.query("SELECT `raid_leader_role` FROM `serverSettings` WHERE `serverId` = ?",
                        new String[]{serverId});
                if (results.getResults().next()) {
                    raidLeaderRoleCache.put(serverId, results.getResults().getString("raid_leader_role"));
                    return raidLeaderRoleCache.get(serverId);
                } else {
                    return "Raid Leader";
                }
            } catch (Exception e) {
                return "Raid Leader";
            }
        }
    }

    /**
     * Set the raid leader role for a server. This also updates it in SQLite
     * @param serverId The server ID
     * @param role The role name
     */
    public void setRaidLeaderRole(String serverId, String role) {
        raidLeaderRoleCache.put(serverId, role);
        try {
            db.update("INSERT INTO `serverSettings` (`serverId`,`raid_leader_role`) VALUES (?,?)",
                    new String[] { serverId, role});
        } catch (SQLException e) {
            //TODO: There is probably a much better way of doing this
            try {
                db.update("UPDATE `serverSettings` SET `raid_leader_role` = ? WHERE `serverId` = ?",
                        new String[] { role, serverId });
            } catch (SQLException e1) {
                // Not much we can do if there is also an insert error
            }
        }
    }

    /**
     * Get the current instance of the bot
     * @return The current instance of the bot.
     */
    public static RaidBot getInstance() {
        return instance;
    }
    
    /**
     * Writes a message to the user notifying them that they have an active chat already 
     * @param user the user
     * @param actvId the type of active acticity
     */
    public static void writeNotificationActiveChat(User user, int actvId) {
    	String actvName;
    	if (actvId == 1) actvName = "role selection";
    	else if (actvId == 2) actvName = "role deselection";
    	else if (actvId == 3) actvName = "create event";
    	else if (actvId == 4) actvName = "edit event";
    	else if (actvId == 5) actvName = "swap role";
    	else actvName = "";
    	
    	user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("You already have an active chat with me (" + actvName + "). Finish it first!").queue());
    }

    /**
     * Determines whether the user has an active chat with the bot which is waiting for DM input
     * @param id the user's id
     * @return 0: no active chat, 1: role selection, 2: role deselection, 3: event creation, 4: event edit
     */
	public int userHasActiveChat(String id) {
		int actvId = 0;
		if (roleSelection.get(id) != null) actvId = 1;
		else if (roleDeselection.get(id) != null) actvId = 2;
		else if (creation.get(id) != null) actvId = 3;
		else if (edits.get(id) != null) actvId = 4;
		else if (roleSwap.get(id) != null) actvId = 5;
		
		return actvId;
	}
	
    /**
     * Get the fractal creator role for a specific server.
     * This works by caching the role once it's retrieved once, and returning the default if a server hasn't set one.
     * @param serverId the ID of the server
     * @return The name of the role that is considered the fractal creator for that server
     */
    public String getFractalCreatorRole(String serverId) {
        if (fractalCreatorRoleCache.get(serverId) != null) {
            return fractalCreatorRoleCache.get(serverId);
        } else {
            try {
                QueryResult results = db.query("SELECT `fractal_creator_role` FROM `serverSettings` WHERE `serverId` = ?",
                        new String[]{serverId});
                if (results.getResults().next()) {
                	fractalCreatorRoleCache.put(serverId, results.getResults().getString("fractal_creator_role"));
                    return fractalCreatorRoleCache.get(serverId);
                } else {
                    return "Fractal Creator";
                }
            } catch (Exception e) {
                return "Fractal Creator";
            }
        }
    }

    /**
     * Set the fractal creator role for a server. This also updates it in SQLite
     * @param serverId The server ID
     * @param role The role name
     */
    public void setFractalCreatorRole(String serverId, String role) {
        fractalCreatorRoleCache.put(serverId, role);
        try {
            db.update("INSERT INTO `serverSettings` (`serverId`,`fractal_creator_role`) VALUES (?,?)",
                    new String[] { serverId, role});
        } catch (SQLException e) {
            //TODO: There is probably a much better way of doing this
            try {
                db.update("UPDATE `serverSettings` SET `fractal_creator_role` = ? WHERE `serverId` = ?",
                        new String[] { role, serverId });
            } catch (SQLException e1) {
                // Not much we can do if there is also an insert error
            }
        }
    }
    
    
    /**
     * Set a channel for a server. This also updates it in SQLite
     * @param serverId The server ID
     * @param channel The channel name
     * @return 0: valid channel, 1: no channel found, 2: cannot write in channel
     */
    private int setChannel(String serverId, String channel, ChannelType type) {
    	// check if channel exists
    	if (checkChannel(serverId, channel) == false)
    		return 1;
    	
    	// check if a message can be written into the channel
    	Guild guild = RaidBot.getInstance().getServer(serverId);
        List<TextChannel> channels = guild.getTextChannelsByName(RaidBot.getInstance().getChannel(serverId, type), true);
        if(channels.size() > 0) {
            // We always go with the first channel if there is more than one
            if (channels.get(0).canTalk() == false)
            	return 2;
            // TODO:
            // check if we have "embed links" permission required to post embedded event messages
        }
    	
        String dbField = "";
        if (type == ChannelType.ARCHIVE)
        {
        	archiveChannelCache.put(serverId, channel);
        	dbField = "archive_channel";
        }
        else if (type == ChannelType.FRACTALS)
        {
        	fractalChannelCache.put(serverId,  channel);
        	dbField = "fractal_channel";
        }
        else if (type == ChannelType.AUTOEVENTS)
        {
        	
        	
        }        
        
        try {
            db.update("INSERT INTO `serverSettings` (`serverId`,`" + dbField + "`) VALUES (?,?)",
                    new String[] { serverId, channel});
        } catch (SQLException e) {
            //TODO: There is probably a much better way of doing this
            try {
                db.update("UPDATE `serverSettings` SET `" + dbField + "` = ? WHERE `serverId` = ?",
                        new String[] { channel, serverId });
            } catch (SQLException e1) {
                // Not much we can do if there is also an update error
            }
        }
        return 0;
    }
    
    /**
     * Get a channel for a specific server.
     * This works by caching the channel once it's retrieved once, and returning the default if a server hasn't set one.
     * @param serverId the ID of the server
     * @return The name of the channel that is considered the archive channel for that server
     */
    private String getChannel(String serverId, ChannelType type) {
        String cached = null;
        String dbField = "";
        if (type == ChannelType.ARCHIVE)
        {
        	cached = archiveChannelCache.get(serverId);
        	dbField = "archive_channel";
        }
        else if (type == ChannelType.FRACTALS)
        {
        	cached = fractalChannelCache.get(serverId);
        	dbField = "fractal_channel";
        }
        else if (type == ChannelType.AUTOEVENTS)
        {
        }    
        
    	if (cached != null) {
            return cached;
        } else {
            try {
                QueryResult results = db.query("SELECT `" + dbField + "` FROM `serverSettings` WHERE `serverId` = ?",
                        new String[]{serverId});
                if (results.getResults().next()) {
                	String result = results.getResults().getString(dbField);
                	if (result != null) {
                		if (type == ChannelType.ARCHIVE)
                			archiveChannelCache.put(serverId, result);
                		else if (type == ChannelType.ARCHIVE)
                			fractalChannelCache.put(serverId, result);
                		else if (type == ChannelType.AUTOEVENTS)
                		{
                			
                		}
                		return result; 
                    }
                	else 
                		return "dummy-channel";
                } else {
                    return "dummy-channel";
                }
            } catch (Exception e) {
                return "dummy-channel";
            }
        }
    }
    
    /**
     * Set the fractal announcement channel for a server. This also updates it in SQLite
     * @param serverId The server ID
     * @param channel The channel name
     * @return 0: valid channel, 1: no channel found, 2: cannot write in channel
     */
    public int setFractalChannel(String serverId, String channel) {
    	return setChannel(serverId, channel, ChannelType.FRACTALS);
    }
    
    /**
     * Get the fractal announcement channel for a specific server.
     * This works by caching the channel once it's retrieved once, and returning the default if a server hasn't set one.
     * @param serverId the ID of the server
     * @return The name of the channel that is considered the fractal announcement channel for that server
     */
    public String getFractalChannel(String serverId) {
        return getChannel(serverId, ChannelType.FRACTALS);
    }
    
    /**
     * Set the archive channel for a server. This also updates it in SQLite
     * @param serverId The server ID
     * @param channel The channel name
     * @return 0: valid channel, 1: no channel found, 2: cannot write in channel
     */
    public int setArchiveChannel(String serverId, String channel) {
    	return setChannel(serverId, channel, ChannelType.ARCHIVE);
    }
    
    /**
     * Get the archive channel for a specific server.
     * This works by caching the channel once it's retrieved once, and returning the default if a server hasn't set one.
     * @param serverId the ID of the server
     * @return The name of the channel that is considered the archive channel for that server
     */
    public String getArchiveChannel(String serverId) {
        return getChannel(serverId, ChannelType.ARCHIVE);
    }
    
    /** 
     * checks if a given channel exists
     * @param serverId the id of the server to be checked 
     * @param channelName the channel name
     * @return true if channel is valid, false otherwise
     */
    public boolean checkChannel(String serverId, String channelName) {      
    	boolean validChannel = false;
    	for (TextChannel channel : getServer(serverId).getTextChannels()) {
            if(channel.getName().replace("#","").equalsIgnoreCase(channelName)) {
                validChannel = true;
            }
        }
        return validChannel;
    }

    /**
     * checks if a valid archive channel is available for a server
     *
     * @param serverId 
     * @return whether a valid archive channel is available
     */
	public boolean isArchiveAvailable(String serverId) {
		return checkChannel(serverId, getArchiveChannel(serverId));
	}

}
