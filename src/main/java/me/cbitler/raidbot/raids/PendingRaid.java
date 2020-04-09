package me.cbitler.raidbot.raids;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to store data about a raid that is being set up
 * This isn't commented as the method names should be self-explanatory
 * @author Christopher Bitler
 */
public class PendingRaid {
    String name, description, date, time, announcementChannel, serverId, leaderId;
    List<RaidRole> rolesWithNumbers = new ArrayList<RaidRole>();
    List<String> permittedDiscordRoles = new ArrayList<String>();
    
    /* *
     * open world events only have a single role (Participants) and users sign up without any class
     */
    boolean isOpenWorld;
    
    /* *
     * whether to display the short version of the raid message
     */
    boolean isDisplayShort;
    
    /* *
     * whether the event is a fractal. Fractal events will not be archived and they can only be displayed as short message.
     */
    boolean isFractalEvent;
    
    public PendingRaid() {
    	this.isFractalEvent = false;
    }
    
    public boolean isOpenWorld() {
    	return isOpenWorld;
    }
    
    public void setOpenWorld(boolean isOpenWorld) {
    	this.isOpenWorld = isOpenWorld;
    }
    
    public boolean isDisplayShort() {
    	return isDisplayShort;
    }
    
    public void setDisplayShort(boolean isDisplayShort) {
    	this.isDisplayShort = isDisplayShort;
    }
    
    public boolean isFractalEvent() {
    	return isFractalEvent;
    }
    
    public void setFractalEvent(boolean isFractalEvent) {
    	this.isFractalEvent = isFractalEvent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAnnouncementChannel() {
        return announcementChannel;
    }

    public void setAnnouncementChannel(String announcementChannel) {
        this.announcementChannel = announcementChannel;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(String leaderName) {
        this.leaderId = leaderName;
    }

    public List<RaidRole> getRolesWithNumbers() {
        return rolesWithNumbers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<String> getPermittedDiscordRoles() {
    	return permittedDiscordRoles;
    }

	public boolean existsRole(String roleName) {
		for (RaidRole role : rolesWithNumbers) {
			if (role.getName().equalsIgnoreCase(roleName)) {
				return true;
			}
		}
		return false;
	}

	public void addTemplateRoles(RaidRole[] raidRoles) {
		for (int r = 0; r < raidRoles.length; r++) {
        	RaidRole role = raidRoles[r];
            rolesWithNumbers.add(new RaidRole(role.getAmount(), role.getName()));
        }
	}

	public void addPermittedDiscordRoles(String role) {
		if (permittedDiscordRoles.contains(role) == false)
			permittedDiscordRoles.add(role);
	}
	
	public void addPermittedDiscordRoles(List<String> roles) {
		for (int r = 0; r < roles.size(); r++) 
		{
			if (permittedDiscordRoles.contains(roles.get(r)) == false)
				permittedDiscordRoles.add(roles.get(r));
		}
	}

	public void clearPermittedDiscordRoles() {
		permittedDiscordRoles.clear();
	}
}
