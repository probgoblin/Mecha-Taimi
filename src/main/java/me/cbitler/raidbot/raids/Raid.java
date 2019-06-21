package me.cbitler.raidbot.raids;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.database.Database;
import me.cbitler.raidbot.utility.Reactions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.sql.SQLException;
import java.util.*;

/**
 * Represents a raid and has methods for adding/removing users, user flex roles,
 * etc
 */
public class Raid {
    String messageId, name, description, date, time, serverId, channelId, raidLeaderName;
    List<RaidRole> roles = new ArrayList<RaidRole>();
    HashMap<RaidUser, String> userToRole = new HashMap<RaidUser, String>();
    HashMap<RaidUser, List<FlexRole>> usersToFlexRoles = new HashMap<>();

    /* *
     * open world events only have a single role (Participants) and users sign up without any class
     */
    boolean isOpenWorld;

    /**
     * Create a new Raid with the specified data
     *
     * @param messageId      The embedded message Id related to this raid
     * @param serverId       The serverId that the raid is on
     * @param channelId      The announcement channel's id for this raid
     * @param raidLeaderName The name of the raid leader
     * @param name           The name of the raid
     * @param date           The date of the raid
     * @param time           The time of the raid
     */
    public Raid(String messageId, String serverId, String channelId, String raidLeaderName, String name,
            String description, String date, String time, boolean isOpenWorld) {
        this.messageId = messageId;
        this.serverId = serverId;
        this.channelId = channelId;
        this.raidLeaderName = raidLeaderName;
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.isOpenWorld = isOpenWorld;
    }

    /**
     * The open world flag for this event
     *
     * @return open world flag for this event
     */
    public boolean isOpenWorld() {
        return isOpenWorld;
    }

    /**
     * Get the message ID for this raid
     *
     * @return The message ID for this raid
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Get the server ID for this raid
     *
     * @return The server ID for this raid
     */
    public String getServerId() {
        return serverId;
    }

    /**
     * Get the channel ID for this raid
     *
     * @return The channel ID for this raid
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * Get the name of this raid
     *
     * @return The name of this raid
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the raid
     *
     * @param name The name of the raid
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Updates the name of the raid in the database
     */
    public boolean updateNameDB() {
        try {
            RaidBot.getInstance().getDatabase().update("UPDATE `raids` SET `name`=? WHERE `raidId`=?",
                    new String[] { name, messageId });
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Set the leader of the raid
     *
     * @param leader The leader of the raid
     */
    public void setLeader(String leader) {
        this.raidLeaderName = leader;
    }

    /**
     * Updates the leader of the raid in the database
     */
    public boolean updateLeaderDB() {
        try {
            RaidBot.getInstance().getDatabase().update("UPDATE `raids` SET `leader`=? WHERE `raidId`=?",
                    new String[] { raidLeaderName, messageId });
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Get the description of the raid
     *
     * @return The description of the raid
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the raid
     *
     * @param description The description of the raid
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Updates the description of the raid in the database
     */
    public boolean updateDescriptionDB() {
        try {
            RaidBot.getInstance().getDatabase().update("UPDATE `raids` SET `description`=? WHERE `raidId`=?",
                    new String[] { description, messageId });
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Get the date of this raid
     *
     * @return The date of this raid
     */
    public String getDate() {
        return date;
    }

    /**
     * Set the date of the raid
     *
     * @param date The date of the raid
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Updates the date of the raid in the database
     */
    public boolean updateDateDB() {
        try {
            RaidBot.getInstance().getDatabase().update("UPDATE `raids` SET `date`=? WHERE `raidId`=?",
                    new String[] { date, messageId });
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Get the time of this raid
     *
     * @return The time of this raid
     */
    public String getTime() {
        return time;
    }

    /**
     * Set the time of the raid
     *
     * @param time The time of the raid
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Updates the time of the raid in the database
     */
    public boolean updateTimeDB() {
        try {
            RaidBot.getInstance().getDatabase().update("UPDATE `raids` SET `time`=? WHERE `raidId`=?",
                    new String[] { time, messageId });
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Get the raid leader's name
     *
     * @return The raid leader's name
     */
    public String getRaidLeaderName() {
        return raidLeaderName;
    }

    /**
     * Get the list of roles in this raid
     *
     * @return The list of roles in this raid
     */
    public List<RaidRole> getRoles() {
        return roles;
    }

    /**
     * Add a new role to the event
     * @param newrole new raid role
     * @return 0 success, 1 role exists, 2 SQL error
     */
    public int addRole(RaidRole newrole) {
        for (RaidRole role : roles) {
            if (role.getName().equalsIgnoreCase(newrole.getName())) {
                return 1;
            }
        }
        roles.add(newrole);

        String rolesString = RaidManager.formatRolesForDatabase(roles);
        try {
            RaidBot.getInstance().getDatabase().update("UPDATE `raids` SET `roles`=? WHERE `raidId`=?",
                    new String[] { rolesString, messageId });
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 2;
        }
    }


    /**
     * Rename a role of the event
     * @param role id
     * @param newname new name for the role
     * @return 0 success, 1 role exists, 2 SQL error
     */
    public int renameRole(int id, String newname) {
        for (RaidRole role : roles) {
            if (role.getName().equalsIgnoreCase(newname)) {
                return 1;
            }
        }
        String oldName = roles.get(id).getName();
        roles.get(id).setName(newname);

        // iterate over users' roles and rename
        for (Map.Entry<RaidUser, String> user : userToRole.entrySet()) {
            if (user.getValue().equals(oldName))
                user.setValue(newname);
        }
        for (Map.Entry<RaidUser, List<FlexRole>> flex : usersToFlexRoles.entrySet()) {
            for (FlexRole frole : flex.getValue()) {
                if (frole.getRole().equals(oldName))
                    frole.setRole(newname);
            }
        }

        // rename in database
        String rolesString = RaidManager.formatRolesForDatabase(roles);
        try {
            Database db = RaidBot.getInstance().getDatabase();
            db.update("UPDATE `raids` SET `roles`=? WHERE `raidId`=?",
                    new String[] { rolesString, messageId });
            db.update("UPDATE `raidUsers` SET `role`=? WHERE `role`=? AND `raidId`=?",
                    new String[] { newname, oldName, messageId });
            db.update("UPDATE `raidUsersFlexRoles` SET `role`=? WHERE `role`=? AND `raidId`=?",
                    new String[] { newname, oldName, messageId });

            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 2;
        }
    }


    /**
     * Change amount for a role of the event
     * @param role id
     * @param newamount new amount for the role
     * @return 0 success, 1 number of users > new amount, 2 SQL error
     */
    public int changeAmountRole(int id, int newamount) {
        String roleName = roles.get(id).getName();
        int numberUsers = getUserNumberInRole(roleName);
        if (newamount < numberUsers)
            return 1;

        roles.get(id).setAmount(newamount);

        // rename in database
        String rolesString = RaidManager.formatRolesForDatabase(roles);
        try {
            Database db = RaidBot.getInstance().getDatabase();
            db.update("UPDATE `raids` SET `roles`=? WHERE `raidId`=?",
                    new String[] { rolesString, messageId });
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 2;
        }
    }

    /**
     * Change flex only status of a role
     * @param role id
     * @param newStatus new amount for the role
     * @return 0 success, 1 number of users > 0 when enabling flexOnly, 2 SQL error
     */
    public int changeFlexOnlyRole(int id, boolean newStatus) {
        String roleName = roles.get(id).getName();
        int numberUsers = getUserNumberInRole(roleName);
        if (0 < numberUsers)
            return 1;

        roles.get(id).setFlexOnly(newStatus);

        // rename in database
        String rolesString = RaidManager.formatRolesForDatabase(roles);
        try {
            Database db = RaidBot.getInstance().getDatabase();
            db.update("UPDATE `raids` SET `roles`=? WHERE `raidId`=?",
                    new String[] { rolesString, messageId });
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 2;
        }
    }


    /**
     * Delete a role from the event
     * @param role id
     * @return 0 success, 1 number of users > 0, 2 SQL error
     */
    public int deleteRole(int id) {
        String roleName = roles.get(id).getName();
        int numberUsers = getUserNumberInRole(roleName);
        int numberUsersFlex = getUserNumberInFlexRole(roleName);

        if (numberUsers > 0 || numberUsersFlex > 0)
            return 1;

        roles.remove(id);

        // delete in database
        String rolesString = RaidManager.formatRolesForDatabase(roles);
        try {
            Database db = RaidBot.getInstance().getDatabase();
            db.update("UPDATE `raids` SET `roles`=? WHERE `raidId`=?",
                    new String[] { rolesString, messageId });
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 2;
        }
    }


    /**
     * Check if a specific role is valid, and whether or not it's full
     *
     * @param role The role to check
     * @return True if it is valid and not full, false otherwise
     */
    public boolean isValidNotFullRole(String role) {
        return this.isValidNotFullRole(role, false);
    }

    /**
     * Check if a specific role is valid, and whether or not it's full
     *
     * @param role The role to check
     * @return True if it is valid and not full, false otherwise
     */
    public boolean isValidNotFullRole(String role, boolean flex) {
        RaidRole r = getRole(role);

        if (r != null) {
            if(r.isFlexOnly() && !flex) return false;
            int max = r.getAmount();
            if (getUserNumberInRole(role) < max) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check to see if a role is valid
     *
     * @param role The role name
     * @return True if the role is valid, false otherwise
     */
    public boolean isValidRole(String role) {
        return getRole(role) != null;
    }

    /**
     * Get the object representing a role
     *
     * @param role The name of the role
     * @return The object representing the specified role
     */
    public RaidRole getRole(String role) {
        for (RaidRole r : roles) {
            if (r.getName().equalsIgnoreCase(role)) {
                return r;
            }
        }

        return null;
    }

    /**
     * Get the number of users in a role
     *
     * @param role The name of the role
     * @return The number of users in the role
     */
    private int getUserNumberInRole(String role) {
        int inRole = 0;
        for (Map.Entry<RaidUser, String> entry : userToRole.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(role))
                inRole += 1;
        }

        return inRole;
    }

    /**
     * Get the number of users in a flex role
     *
     * @param role The name of the role
     * @return The number of users in the role
     */
    private int getUserNumberInFlexRole(String role) {
        int inRole = 0;
        for (Map.Entry<RaidUser, List<FlexRole>> flex : usersToFlexRoles.entrySet()) {
            if (flex.getKey() != null) {
                for (FlexRole frole : flex.getValue()) {
                    if (frole.getRole().equalsIgnoreCase(role))
                        inRole += 1;
                }
            }
        }

        return inRole;
    }

    /**
     * Get list of users in a role
     *
     * @param role The name of the role
     * @return The users in the role
     */
    public List<RaidUser> getUsersInRole(String role) {
        List<RaidUser> users = new ArrayList<>();
        for (Map.Entry<RaidUser, String> entry : userToRole.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(role)) {
                users.add(entry.getKey());
            }
        }

        return users;
    }

    /**
     * Add a user to this raid. This first creates the user and attempts to insert
     * it into the database, if needed Then it adds them to list of raid users with
     * their role
     *
     * @param id        The id of the user
     * @param name      The name of the user
     * @param spec      The specialization they are playing
     * @param role      The role they will be playing in the raid
     * @param db_insert Whether or not the user should be inserted. This is false
     *                  when the roles are loaded from the database.
     * @return true if the user was added, false otherwise
     */
    public boolean addUser(String id, String name, String spec, String role, boolean db_insert,
            boolean update_message) {
        RaidUser user = new RaidUser(id, name, spec, role);

        if (db_insert) {
            try {
                RaidBot.getInstance().getDatabase()
                        .update("INSERT INTO `raidUsers` (`userId`, `username`, `spec`, `role`, `raidId`)"
                                + " VALUES (?,?,?,?,?)", new String[] { id, name, spec, role, this.messageId });
            } catch (SQLException e) {
                return false;
            }
        }

        userToRole.put(user, role);
        usersToFlexRoles.computeIfAbsent(new RaidUser(id, name, "", ""), k -> new ArrayList<FlexRole>());

        if (update_message) {
            updateMessage();
        }
        return true;
    }

    /**
     * Add a user to a flex role in this raid. This first creates the user and
     * attempts to insert it into the database, if needed Then it adds them to list
     * of raid users' flex roles with their flex role
     *
     * @param id        The id of the user
     * @param name      The name of the user
     * @param spec      The specialization they are playing
     * @param role      The flex role they will be playing in the raid
     * @param db_insert Whether or not the user should be inserted. This is false
     *                  when the roles are loaded from the database.
     * @return true if the user was added, false otherwise
     */
    public boolean addUserFlexRole(String id, String name, String spec, String role, boolean db_insert,
            boolean update_message) {
        RaidUser user = new RaidUser(id, name, "", "");
        FlexRole frole = new FlexRole(spec, role);

        if (db_insert) {
            try {
                RaidBot.getInstance().getDatabase()
                        .update("INSERT INTO `raidUsersFlexRoles` (`userId`, `username`, `spec`, `role`, `raidId`)"
                                + " VALUES (?,?,?,?,?)", new String[] { id, name, spec, role, this.messageId });
            } catch (Exception e) {
                return false;
            }
        }

        if (usersToFlexRoles.get(user) == null) {
            usersToFlexRoles.put(user, new ArrayList<FlexRole>());
        }

        usersToFlexRoles.get(user).add(frole);
        if (update_message) {
            updateMessage();
        }
        return true;
    }

    /**
     * Add a user to this open world event with the default role
     *
     * @param id        The id of the user
     * @param name      The name of the user
     * @return true if the user was added, false otherwise
     */
    public boolean addUserOpenWorld(String id, String name) {
        boolean success = false;

        String roleName = roles.get(0).getName();
        if (isValidNotFullRole(roleName)) // there is still space
            success = addUser(id, name, "", roleName, true, true);
        else
            success = addUserFlexRole(id, name, "", roleName, true, true);

        return success;
    }

    /**
     * Check if a specific user is in this raid (main roles)
     *
     * @param id The id of the user
     * @return True if they are in the raid, false otherwise
     */
    public boolean isUserInRaid(String id) {
        for (Map.Entry<RaidUser, String> entry : userToRole.entrySet()) {
            if (entry.getKey().getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove a user from this raid. This also updates the database to remove them
     * from the raid and any flex roles they are in
     *
     * @param id The user's id
     */
    public boolean removeUser(String id) {
        boolean found = false;
        Iterator<Map.Entry<RaidUser, String>> users = userToRole.entrySet().iterator();
        while (users.hasNext()) {
            Map.Entry<RaidUser, String> user = users.next();
            if (user.getKey().getId().equalsIgnoreCase(id)) {
                users.remove();
                found = true;
            }
        }

        Iterator<Map.Entry<RaidUser, List<FlexRole>>> usersFlex = usersToFlexRoles.entrySet().iterator();
        while (usersFlex.hasNext()) {
            Map.Entry<RaidUser, List<FlexRole>> userFlex = usersFlex.next();
            if (userFlex.getKey().getId().equalsIgnoreCase(id)) {
                usersFlex.remove();
                found = true;
            }
        }

        try {
            RaidBot.getInstance().getDatabase().update("DELETE FROM `raidUsers` WHERE `userId` = ? AND `raidId` = ?",
                    new String[] { id, getMessageId() });
            RaidBot.getInstance().getDatabase().update(
                    "DELETE FROM `raidUsersFlexRoles` WHERE `userId` = ? and `raidId` = ?",
                    new String[] { id, getMessageId() });
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (found)
            updateMessage();

        return found;
    }

    /**
     * Send the dps report log links to the players in this raid
     *
     * @param logLinks The list of links
     */
    public void messagePlayersWithLogLinks(List<String> logLinks) {
        String logLinkMessage = "ArcDPS reports from **" + this.getName() + "**:\n";
        for (String link : logLinks) {
            logLinkMessage += (link + "\n");
        }

        final String finalLogLinkMessage = logLinkMessage;
        for (RaidUser user : this.userToRole.keySet()) {
            RaidBot.getInstance().getServer(this.serverId).getMemberById(user.id).getUser().openPrivateChannel()
                    .queue(privateChannel -> privateChannel.sendMessage(finalLogLinkMessage).queue());
        }
    }

    /**
     * Update the embedded message for the raid
     */
    public void updateMessage() {
        MessageEmbed embed = buildEmbed();
        try {
            RaidBot.getInstance().getServer(getServerId()).getTextChannelById(getChannelId())
                    .editMessageById(getMessageId(), embed).queue();
        } catch (Exception e) {
        }
    }

    /**
     * Build the embedded message that shows the information about this raid
     *
     * @return The embedded message representing this raid
     */
    private MessageEmbed buildEmbed() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(getName());
        builder.addField("Description:", getDescription(), false);
        builder.addBlankField(false);
        if (getRaidLeaderName() != null) {
            builder.addField("Leader: ", "**" + getRaidLeaderName() + "**", false);
        }
        builder.addBlankField(false);
        builder.addField("Date: ", getDate(), true);
        builder.addField("Time: ", getTime(), true);
        builder.addBlankField(false);
        builder.addField("Roles:", buildRolesText(), true);
        builder.addField("Flex Roles:", buildFlexRolesText(), true);
        builder.addBlankField(false);
        builder.addField("ID: ", messageId, false);

        return builder.build();
    }

    /**
     * Build the flex roles text, which includes a list of flex roles users are
     * playing and their specs
     *
     * @return The flex role text
     */
    private String buildFlexRolesText() {
        String text = "";
        if (isOpenWorld) {
            for (Map.Entry<RaidUser, List<FlexRole>> flex : usersToFlexRoles.entrySet()) {
                if (flex.getKey() != null && flex.getValue().isEmpty() == false)
                    text += ("- " + flex.getKey().getName() + "\n");
            }
        } else {
            // collect names and specializations for each role
            Map<String, List<RaidUser>> flexUsersByRole = new HashMap<String, List<RaidUser>>();
            for (int r = 0; r < roles.size(); r++) {
                flexUsersByRole.put(roles.get(r).getName(), new ArrayList<RaidUser>());
            }

            for (Map.Entry<RaidUser, List<FlexRole>> flex : usersToFlexRoles.entrySet()) {
                if (flex.getKey() != null) {
                    for (FlexRole frole : flex.getValue()) {
                        flexUsersByRole.get(frole.getRole()).add(new RaidUser(flex.getKey().id, flex.getKey().name, frole.spec, null));
                    }
                }
            }
            for (int r = 0; r < roles.size(); r++) {
                String roleName = roles.get(r).getName();
                text += (roleName + ": \n");

                for (RaidUser user : flexUsersByRole.get(roleName)) {
                        Emote userEmote = Reactions.getEmoteByName(user.getSpec());
                        if(userEmote == null)
                            text += ("- " + user.getName() + " (" + user.getSpec() + ")\n");
                        else
                            text += ("<:"+userEmote.getName()+":"+userEmote.getId()+"> " + user.getName() + " (" + user.getSpec() + ")\n");
                }
                text += "\n";
            }
        }

        return text;
    }

    /**
     * Build the role text, which shows the roles users are playing in the raids
     *
     * @return The role text
     */
    private String buildRolesText() {
        String text = "";
        for (RaidRole role : roles) {
            if(role.isFlexOnly()) continue;
            List<RaidUser> raidUsersInRole = getUsersInRole(role.name);
            text += ("**" + role.name + " ( " + raidUsersInRole.size() + " / " + role.amount + " ):** \n");
            for (RaidUser user : raidUsersInRole) {
                if (isOpenWorld) {
                    text += ("- " + user.name + "\n");
                } else {
                    Emote userEmote = Reactions.getEmoteByName(user.spec);
                    if(userEmote == null)
                        text += "   - " + user.name + " (" + user.spec + ")\n";
                    else
                        text += "   <:"+userEmote.getName()+":"+userEmote.getId()+"> " + user.name + " (" + user.spec + ")\n";
                }
            }
            text += "\n";
        }
        return text;
    }

    /**
     * Get a List of RaidUsers from main roles in this raid by their ID
     *
     * @param name The user's ID
     * @return The List of RaidUsers if they are in this raid, null otherwise
     */
    public ArrayList<RaidUser> getRaidUsersById(String id) {
        ArrayList<RaidUser> raidUsers = new ArrayList<RaidUser>();
        for (RaidUser user : userToRole.keySet()) {
            if (user.getId().equalsIgnoreCase(id)) {
                raidUsers.add(user);
            }
        }
        return raidUsers;
    }

    /**
     * Get a List of RaidUsers from flex roles in this raid by their ID
     *
     * @param name The user's ID
     * @return The List of RaidUsers if they are in this raid, null otherwise
     */
    public ArrayList<FlexRole> getRaidUsersFlexRolesById(String id) {
        ArrayList<FlexRole> raidRoles = new ArrayList<FlexRole>();
        for (RaidUser user : usersToFlexRoles.keySet()) {
            if (user.getId().equalsIgnoreCase(id)) {
                for(FlexRole fRole : usersToFlexRoles.get(user)){
                    raidRoles.add(fRole);
                }
            }
        }
        return raidRoles;
    }

    /**
     * Remove a user by their username
     *
     * @param name The name of the user being removed
     */
    public void removeUserByName(String name) {
        String idToRemove = "";
        for (Map.Entry<RaidUser, String> entry : userToRole.entrySet()) {
            if (entry.getKey().name.equalsIgnoreCase(name)) {
                idToRemove = entry.getKey().id;
                break;
            }
        }
        if (idToRemove.isEmpty()) { // did not find the user in main roles, check flex roles
            for (Map.Entry<RaidUser, List<FlexRole>> entry : usersToFlexRoles.entrySet()) {
                if (entry.getKey().name.equalsIgnoreCase(name)) {
                    idToRemove = entry.getKey().id;
                    break;
                }
            }
        }

        removeUser(idToRemove);
    }


    /**
     * Remove a user from their main role
     *
     * @param id The id of the user being removed
     */
    public void removeUserFromMainRoles(String id) {
        Iterator<Map.Entry<RaidUser, String>> users = userToRole.entrySet().iterator();
        while (users.hasNext()) {
            Map.Entry<RaidUser, String> user = users.next();
            if (user.getKey().getId().equalsIgnoreCase(id)) {
                users.remove();
            }
        }

        try {
            RaidBot.getInstance().getDatabase().update("DELETE FROM `raidUsers` WHERE `userId` = ? AND `raidId` = ?",
                    new String[] { id, getMessageId() });
        } catch (SQLException e) {
            e.printStackTrace();
        }

        updateMessage();
    }


    /**
     * Remove a user from their main role
     *
     * @param id The id of the user being removed
     * @param role The role that should be removed
     * @param spec The class specialization that should be removed
     * @return true if user was signed up for this role and class, false otherwise
     */
    public boolean removeUserFromFlexRoles(String id, String role, String spec) {
        boolean found = false;
        Iterator<Map.Entry<RaidUser, List<FlexRole>>> users = usersToFlexRoles.entrySet().iterator();
        while (users.hasNext()) {
            Map.Entry<RaidUser, List<FlexRole>> user = users.next();
            if (user.getKey().getId().equalsIgnoreCase(id)) {
                Iterator<FlexRole> froles = user.getValue().iterator();
                while (froles.hasNext()) {
                    FlexRole frole = froles.next();
                    if (frole.getSpec().equals(spec) && frole.getRole().equals(role)) {
                        froles.remove();
                        found = true;
                    }
                }
            }
        }

        try {
            RaidBot.getInstance().getDatabase().update(
                    "DELETE FROM `raidUsersFlexRoles` WHERE `userId` = ? and `raidId` = ? and `role` = ? and `spec` = ?",
                    new String[] { id, getMessageId(), role, spec });
        } catch (SQLException e) {
            e.printStackTrace();
        }

        updateMessage();
        return found;
    }


    /**
     * Get the number of flex roles a user has
     *
     * @param id The id of the user
     * @return The number of flex roles that a user has
     */
    public int getUserNumFlexRoles(String id) {
        for (Map.Entry<RaidUser, List<FlexRole>> entry : usersToFlexRoles.entrySet()) {
            RaidUser user = entry.getKey();
            if (user != null && user.getId() != null) {
                if (user.id.equalsIgnoreCase(id)) {
                    return entry.getValue().size();
                }
            }
        }
        return 0;
    }
}
