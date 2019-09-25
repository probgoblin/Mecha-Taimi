package me.cbitler.raidbot.utility;

import me.cbitler.raidbot.RaidBot;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

/**
 * Utility class for handling permissions
 * @author Christopher Bitler
 */
public class PermissionsUtil {
    /**
     * Check to see if a member has a specific role
     * @param member The member to check
     * @param 
     * @return True if they have the role, false if they don't
     */
	public static boolean hasRole(Member member, String rolename) {
        for (Role role : member.getRoles()) {
            if (role.getName().equalsIgnoreCase(rolename)) {
                return true;
            }
        }
        return false;
	}
	
	
    public static boolean hasRaidLeaderRole(Member member) {
        String raidLeaderRole = RaidBot.getInstance().getRaidLeaderRole(member.getGuild().getId());
        return hasRole(member, raidLeaderRole);
    }
    

	public static boolean hasFractalCreatorRole(Member member) {
		String fractalCreatorRole = RaidBot.getInstance().getFractalCreatorRole(member.getGuild().getId());
        return hasRole(member, fractalCreatorRole);
	}
}
