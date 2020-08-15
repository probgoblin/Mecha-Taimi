package me.cbitler.raidbot.swap;

import me.cbitler.raidbot.raids.FlexRole;
import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidUser;
import net.dv8tion.jda.api.entities.User;


public class SwapUtil {

	/**
	 * Converts the user's main role to a flex role. Assumes that the user has a main role.
	 * @param raid
	 * @param userId
	 */
	public static void moveMainToFlex(Raid raid, String userId, boolean update_message) {
		RaidUser mainRole = raid.getRaidUsersById(userId).get(0); // this should never return null
		raid.addUserFlexRole(userId, mainRole.getName(), mainRole.getSpec(), mainRole.getRole(), true, false);
		raid.removeUserFromMainRoles(userId, false);
		if (update_message)
			raid.updateMessage();
	}


	/**
	 * Tries to convert one of the user's flex roles to his main role (and moves the main role to flex if it exists)
	 * @param raid
	 * @param user
	 * @param flexRoleId
	 * @return true, if there was a free spot in the main roles to move the user's flex role to
	 */
	public static boolean moveFlexToMain(Raid raid, User user, int flexRoleId) {
		FlexRole flexRole = raid.getRaidUsersFlexRolesById(user.getId()).get(flexRoleId);
		if (raid.isValidNotFullRole(flexRole.getRole())) {
			if (raid.isUserInRaid(user.getId())) {
				// move old main to flex
				moveMainToFlex(raid, user.getId(), false);
			}
			// move flex to main
			raid.addUser(user.getId(), user.getName(), flexRole.getSpec(), flexRole.getRole(), true, false);
			raid.removeUserFromFlexRoles(user.getId(), flexRole.getRole(), flexRole.getSpec(), true);
			// remove will update the message as well
			return true;
		} else
			return false;
	}
}
