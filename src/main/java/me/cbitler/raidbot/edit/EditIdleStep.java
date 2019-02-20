package me.cbitler.raidbot.edit;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.raids.PendingRaid;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Edit a property for the event
 * @author Franziska Mueller
 */
public class EditIdleStep implements EditStep {

	private String messageID;
	private EditStep nextStep;
	
	public EditIdleStep(String messageId) {
		this.messageID = messageId;
	}
	
    /**
     * Idle step in editing process
     * @param e The direct message event
     * @return True if the user passed the name of an editable property
     */
    public boolean handleDM(PrivateMessageReceivedEvent e) {
        boolean valid = true;
    	if (e.getMessage().getRawContent().equalsIgnoreCase("time")) {
        	nextStep = new EditTimeStep(messageID);
        } else if (e.getMessage().getRawContent().equalsIgnoreCase("date")) {
        	nextStep = new EditDateStep(messageID);
        } else if (e.getMessage().getRawContent().equalsIgnoreCase("name")) {
        	nextStep = new EditNameStep(messageID);
        } else if (e.getMessage().getRawContent().equalsIgnoreCase("description")) {
        	nextStep = new EditDescriptionStep(messageID);
        } else if(e.getMessage().getRawContent().equalsIgnoreCase("done")) {
            nextStep = null;
        }
        else {
        	e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Invalid property. Supported properties: time, date, name, description.").queue());
        	valid = false;
        }
    	return valid;
    }

    /**
     * {@inheritDoc}
     */
    public String getStepText() {
        return "Enter the name of the property you want to change [time, date, name, description] or done when you want to finish editing.";
    }

    /**
     * {@inheritDoc}
     */
    public EditStep getNextStep() {
        return nextStep;
    }

	@Override
	public String getMessageID() {
		return messageID;
	}
}
