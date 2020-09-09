package me.cbitler.raidbot.utility;

//import java.util.List;
//import java.util.ArrayList;
//import java.util.Arrays;

import me.cbitler.raidbot.server_settings.ServerSettings;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

/**
 * Utility class for handling permissions
 * @author Christopher Bitler
 */
public class PermissionsUtil {

//	static String[] discordRoleNames = {
//            "Master Raider", "Adept Raider"
//    };
//
//	public static List<String> getAllDiscordRoleNames() {
//		return new ArrayList<String>(Arrays.asList(discordRoleNames));
//	}

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
        String raidLeaderRole = ServerSettings.getRaidLeaderRole(member.getGuild().getId());
        return hasRole(member, raidLeaderRole);
    }


	public static boolean hasFractalCreatorRole(Member member) {
		String fractalCreatorRole = ServerSettings.getFractalCreatorRole(member.getGuild().getId());
        return hasRole(member, fractalCreatorRole);
	}
}
