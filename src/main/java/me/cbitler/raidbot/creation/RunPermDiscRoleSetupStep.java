package me.cbitler.raidbot.creation;

import java.util.List;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.raids.PendingRaid;
import me.cbitler.raidbot.utility.PermissionsUtil;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Permitted role setup step for the event.
 * Gives a choice between preset roles or manual setup
 * @author Franziska Mueller
 */
public class RunPermDiscRoleSetupStep implements CreationStep {

	List<String> discRoleNames = PermissionsUtil.getAllDiscordRoleNames();

    /**
     * Handle user input.
     * @param e The direct message event
     * @return True if the user entered a valid choice, false otherwise
     */
    public boolean handleDM(PrivateMessageReceivedEvent e) {
    	PendingRaid raid = RaidBot.getInstance().getPendingRaids().get(e.getAuthor().getId());
        if (raid == null)
        	throw new RuntimeException();
    	
    	String[] chosenRoles = e.getMessage().getRawContent().split(",");
    	for (int role = 0; role < chosenRoles.length; role++) {
    		// try to parse an integer
    		try {
        		int choiceId = Integer.parseInt(chosenRoles[role]) - 1;
        		if (choiceId < discRoleNames.size()) { // preset role
        			raid.addPermittedDiscordRoles(discRoleNames.get(choiceId));
        		} else if (choiceId == discRoleNames.size()) { // everyone
        			raid.clearPermittedDiscordRoles(); // make event availabe to everyone
        			return true;
        		}
        	} catch (Exception exp) {
                // it's not an integer but a role name, add it
                raid.addPermittedDiscordRoles(chosenRoles[role]);
        	}
    	}
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String getStepText() {
    	String message = "Choose who should be able to sign up for the event:\n";
    	for (int i = 0; i < discRoleNames.size(); i++) {
        	message += "`" + (i+1) + "` " + discRoleNames.get(i) + "\n";
        }
    	message += "`" + (discRoleNames.size() + 1) + "` *everyone* \n\n"
        		+ "You can also enter a comma-separated list of the choices above and other role names, for example *1,Fractalist*.";
        return message;
    }

    /**
     * {@inheritDoc}
     */
    public CreationStep getNextStep() {
        return null;
    }
}
