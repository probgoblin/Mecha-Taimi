package me.cbitler.raidbot.server_settings;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.database.Database;
import me.cbitler.raidbot.database.QueryResult;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Class representing different server settings.
 *
 * @author Franziska Mueller
 */
public class ServerSettings {

    public enum ChannelType { ARCHIVE, FRACTALS, AUTOEVENTS };

    static HashMap<String, String> raidLeaderRoleCache = new HashMap<>();
    static HashMap<String, String> fractalCreatorRoleCache = new HashMap<>();

    static HashMap<String, String> fractalChannelCache = new HashMap<>();
    static HashMap<String, String> archiveChannelCache = new HashMap<>();
    static HashMap<String, String> autoEventsChannelCache = new HashMap<>();

    static HashMap<String, SortedMap<String, List<String>>> permittedDiscordRoles = new HashMap<>();


    /**
     * Get the raid leader role for a specific server.
     * This works by caching the role once it's retrieved once, and returning the default if a server hasn't set one.
     * @param serverId the ID of the server
     * @return The name of the role that is considered the raid leader for that server
     */
    public static String getRaidLeaderRole(String serverId) {
        if (raidLeaderRoleCache.get(serverId) != null) {
            return raidLeaderRoleCache.get(serverId);
        } else {
            try {
                Database db = RaidBot.getInstance().getDatabase();
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
    public static void setRaidLeaderRole(String serverId, String role) {
        raidLeaderRoleCache.put(serverId, role);
        Database db = RaidBot.getInstance().getDatabase();
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
     * Get the fractal creator role for a specific server.
     * This works by caching the role once it's retrieved once, and returning the default if a server hasn't set one.
     * @param serverId the ID of the server
     * @return The name of the role that is considered the fractal creator for that server
     */
    public static String getFractalCreatorRole(String serverId) {
        if (fractalCreatorRoleCache.get(serverId) != null) {
            return fractalCreatorRoleCache.get(serverId);
        } else {
            try {
                Database db = RaidBot.getInstance().getDatabase();
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
    public static void setFractalCreatorRole(String serverId, String role) {
        fractalCreatorRoleCache.put(serverId, role);
        Database db = RaidBot.getInstance().getDatabase();
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
    public static int setChannel(String serverId, String channel, ChannelType type) {
    	// check if channel exists
    	if (checkChannel(serverId, channel) == false)
    		return 1;

    	// check if a message can be written into the channel
    	Guild guild = RaidBot.getInstance().getServer(serverId);
        List<TextChannel> channels = guild.getTextChannelsByName(getChannel(serverId, type), true);
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
        	autoEventsChannelCache.put(serverId,  channel);
        	dbField = "auto_events_channel";
        }

        Database db = RaidBot.getInstance().getDatabase();
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
    private static String getChannel(String serverId, ChannelType type) {
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
        	autoEventsChannelCache.get(serverId);
        	dbField = "auto_events_channel";
        }

    	if (cached != null) {
            return cached;
        } else {
            try {
                Database db = RaidBot.getInstance().getDatabase();
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
                        	autoEventsChannelCache.get(serverId);
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
    public static int setFractalChannel(String serverId, String channel) {
    	return setChannel(serverId, channel, ChannelType.FRACTALS);
    }

    /**
     * Get the fractal announcement channel for a specific server.
     * This works by caching the channel once it's retrieved once, and returning the default if a server hasn't set one.
     * @param serverId the ID of the server
     * @return The name of the channel that is considered the fractal announcement channel for that server
     */
    public static String getFractalChannel(String serverId) {
        return getChannel(serverId, ChannelType.FRACTALS);
    }

    /**
     * Set the archive channel for a server. This also updates it in SQLite
     * @param serverId The server ID
     * @param channel The channel name
     * @return 0: valid channel, 1: no channel found, 2: cannot write in channel
     */
    public static int setArchiveChannel(String serverId, String channel) {
    	return setChannel(serverId, channel, ChannelType.ARCHIVE);
    }

    /**
     * Get the archive channel for a specific server.
     * This works by caching the channel once it's retrieved once, and returning the default if a server hasn't set one.
     * @param serverId the ID of the server
     * @return The name of the channel that is considered the archive channel for that server
     */
    public static String getArchiveChannel(String serverId) {
        return getChannel(serverId, ChannelType.ARCHIVE);
    }

    /**
     * Get the auto events channel for a specific server.
     * This works by caching the channel once it's retrieved once, and returning the default if a server hasn't set one.
     * @param serverId the ID of the server
     * @return The name of the channel that is considered the auto events channel for that server
     */
    public static String getAutoEventsChannel(String serverId) {
        return getChannel(serverId, ChannelType.AUTOEVENTS);
    }


    public static void removeRoleGroup(String serverId, int groupId) {
    	SortedMap<String, List<String>> serverRoleGroups = permittedDiscordRoles.get(serverId);
    	if (serverRoleGroups != null)
    	{
    		Iterator<String> groupNames = serverRoleGroups.keySet().iterator();
    		try {
    			while (groupId >= 0)
    			{
    				groupNames.next();
    				groupId--;
    			}
    			groupNames.remove();
    		} catch (Exception e) { }
    		updateRoleGroupsDB(serverId);
    	}
    }


    public static boolean addRoleGroup(String serverId, String groupName, List<String> roles) {
    	// first check if all roles exist on this server
    	for (String roleName : roles)
    	{
    		if (checkRole(serverId, roleName) == false)
    			return false;
    	}

    	// add role group
    	SortedMap<String, List<String>> serverRoleGroups = permittedDiscordRoles.get(serverId);
    	if (serverRoleGroups == null)
    		serverRoleGroups = new TreeMap<>();
    	serverRoleGroups.put(groupName, roles);
    	permittedDiscordRoles.put(serverId, serverRoleGroups);
    	updateRoleGroupsDB(serverId);

    	return true;
    }


    private static void updateRoleGroupsDB(String serverId) {
    	SortedMap<String, List<String>> serverRoleGroups = permittedDiscordRoles.get(serverId);
    	Database db = RaidBot.getInstance().getDatabase();
    	if (serverRoleGroups == null)
    	{
    		try {
    			db.update("UPDATE `serverSettings` SET `predef_role_groups` = NULL WHERE `serverId` = ?",
    					new String[]{ serverId });
    		} catch (SQLException e_remove) { }
    	}
    	else
    	{
    		String dbString = convertRoleGroupsToString(serverRoleGroups);
    		try {
    			db.update("INSERT INTO `serverSettings` (`serverId`,`predef_role_groups`) VALUES (?,?)",
                    new String[] { serverId, dbString});
    		} catch (SQLException e) {
    			//TODO: There is probably a much better way of doing this
    			try {
    				db.update("UPDATE `serverSettings` SET `predef_role_groups` = ? WHERE `serverId` = ?",
    						new String[] { dbString, serverId });
    			} catch (SQLException e1) {
    				// Not much we can do if there is also an update error
    			}
    		}
    	}
    }

    private static String convertRoleGroupsToString(SortedMap<String, List<String>> roleGroups) {
    	String result = "";
    	if (roleGroups == null)
    		return result;
    	Iterator<String> groupNamesIt = roleGroups.keySet().iterator();
    	Iterator<List<String>> roleNamesIt = roleGroups.values().iterator();
    	for (int g = 0; g < roleGroups.size(); g++)
    	{
    		result += groupNamesIt.next() + ";";
    		List<String> roles = roleNamesIt.next();
    		for (int r = 0; r < roles.size(); r++)
    		{
    			result += roles.get(r);
    			if (r < roles.size() - 1)
    				result += ",";
    		}

    		if (g < roleGroups.size() - 1)
    			result += "/";
    	}
    	return result;
    }

    private static SortedMap<String, List<String>> convertRoleGroupsFromString(String input) {
    	SortedMap<String, List<String>> result = new TreeMap<>();
    	String[] groupSplits = input.split("/");
    	for (int g = 0; g < groupSplits.length; g++)
    	{
    		String[] nameSplits = groupSplits[g].split(";");
    		if (nameSplits.length != 2)
    			continue;
    		String[] roleSplits = nameSplits[1].split(",");
    		List<String> roleNames = new ArrayList<String>();
    		for (int r = 0; r < roleSplits.length; r++)
    			roleNames.add(roleSplits[r]);
    		result.put(nameSplits[0], roleNames);
    	}
    	return result;
    }

    /**
     * Loads the predefined role groups for a server from the database if not already cached
     * @param serverId
     */
    private static void loadPredefRoleGroups(String serverId) {
    	if (permittedDiscordRoles.get(serverId) != null)
    		return;
    	try {
            Database db = RaidBot.getInstance().getDatabase();
            QueryResult results = db.query("SELECT `predef_role_groups` FROM `serverSettings` WHERE `serverId` = ?",
                    new String[]{serverId});
            if (results.getResults().next()) {
            	String result = results.getResults().getString("predef_role_groups");
            	if (result != null) {
            		// construct sorted map
            		permittedDiscordRoles.put(serverId, convertRoleGroupsFromString(result));
                }
            }
        } catch (Exception e) { }
    }


    /**
     * Returns the set of predefined role groups on this server
     * @param serverId
     * @return list of predefined role groups
     */
    public static Set<String> getPredefGroupNames(String serverId) {
    	loadPredefRoleGroups(serverId);
    	SortedMap<String, List<String>> permRolesServer = permittedDiscordRoles.get(serverId);
    	if (permRolesServer == null)
    		return new HashSet<String>();
    	return permRolesServer.keySet();
    }

    /**
     * Returns the list of discord roles corresponding to a predefined group name
     * @param serverId
     * @param groupId
     * @return list of discord roles
     */
    public static List<String> getPredefGroupRoles(String serverId, int groupId) {
    	loadPredefRoleGroups(serverId);
    	SortedMap<String, List<String>> permRolesServer = permittedDiscordRoles.get(serverId);
    	List<String> result = new ArrayList<String>();
    	if (permRolesServer == null)
    		return result;
    	Iterator<List<String>> it = permRolesServer.values().iterator();
    	try {
    		for (int i = 0; i < groupId; i++)
    			it.next();
    		return it.next();
    	} catch (Exception exc) {
    		return result;
    	}
    }

    /**
     * Returns a list of lists of discord roles corresponding to a all group names
     * @param serverId
     * @return list of lists of discord roles
     */
    public static List<List<String>> getAllPredefGroupRoles(String serverId) {
    	loadPredefRoleGroups(serverId);
    	SortedMap<String, List<String>> permRolesServer = permittedDiscordRoles.get(serverId);
    	List<List<String>> result = new ArrayList<>();
    	if (permRolesServer == null)
    		return result;
    	Iterator<List<String>> it = permRolesServer.values().iterator();
    	while (it.hasNext())
    		result.add(it.next());
    	return result;
    }

    /**
     * checks if a given channel exists
     * @param serverId the id of the server to be checked
     * @param channelName the channel name
     * @return true if channel is valid, false otherwise
     */
    public static boolean checkChannel(String serverId, String channelName) {
    	boolean validChannel = false;
        RaidBot bot = RaidBot.getInstance();
    	for (TextChannel channel : bot.getServer(serverId).getTextChannels()) {
            if(channel.getName().replace("#","").equalsIgnoreCase(channelName)) {
                validChannel = true;
            }
        }
        return validChannel;
    }


    /**
     * checks if a given role exists
     * @param serverId the id of the server to be checked
     * @param roleName the role name
     * @return true if role is valid, false otherwise
     */
    public static boolean checkRole(String serverId, String roleName) {
    	boolean validRole = false;
        RaidBot bot = RaidBot.getInstance();
    	for (Role role : bot.getServer(serverId).getRoles()) {
            if(role.getName().equals(roleName)) {
                validRole = true;
                break;
            }
        }
        return validRole;
    }



    /**
     * checks if a valid archive channel is available for a server
     *
     * @param serverId
     * @return whether a valid archive channel is available
     */
	public static boolean isArchiveAvailable(String serverId) {
		return checkChannel(serverId, getArchiveChannel(serverId));
	}

}
