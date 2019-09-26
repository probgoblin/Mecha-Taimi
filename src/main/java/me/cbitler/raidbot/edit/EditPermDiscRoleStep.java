package me.cbitler.raidbot.edit;

import java.util.List;

import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.utility.PermissionsUtil;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Permitted role setup step for the event.
 * Gives a choice between preset roles or manual setup
 * @author Franziska Mueller
 */
public class EditPermDiscRoleStep implements EditStep {

	List<String> discRoleNames = PermissionsUtil.getAllDiscordRoleNames();
	String messageID;
	Raid raid;
	
	public EditPermDiscRoleStep(String messageId) {
		this.messageID = messageId;
		this.raid = RaidManager.getRaid(messageID);
	}
	
	private String getPermissionString() {
		if (raid.getPermittedDiscordRoles().isEmpty())
        	return "*everyone*";
        else
        	return raid.getPermittedDiscordRoles().toString();
    }

    /**
     * Handle user input.
     * @param e The direct message event
     * @return True if the user entered a valid choice, false otherwise
     */
    public boolean handleDM(PrivateMessageReceivedEvent e) {
    	String[] chosenRoles = e.getMessage().getRawContent().split(",");
        for (int role = 0; role < chosenRoles.length; role++) {
        	// try to parse an integer
        	try {
           		int choiceId = Integer.parseInt(chosenRoles[role]) - 1;
           		if (choiceId < discRoleNames.size()) { // preset role
           			raid.addPermittedDiscordRoles(discRoleNames.get(choiceId));
           		} else if (choiceId == discRoleNames.size()) { // everyone
           			raid.clearPermittedDiscordRoles(); // make event availabe to everyone
           			break;
           		}
           	} catch (Exception exp) {
                // it's not an integer but a role name, add it
                raid.addPermittedDiscordRoles(chosenRoles[role]);
           	}
        }
        String messageNewPerm = "Sign-up is now available for: " + getPermissionString();     
        e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(messageNewPerm).queue());
    	
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String getStepText() {
    	String message = "Sign-up is currently available for " + getPermissionString() + ".\n"
    			+ "Choose who should be added:\n";
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
    public EditStep getNextStep() {
        return new EditIdleStep(messageID);
    }
    
    @Override
	public String getMessageID() {
		return messageID;
	}
}
