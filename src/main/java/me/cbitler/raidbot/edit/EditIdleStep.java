package me.cbitler.raidbot.edit;

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
        if(e.getMessage().getRawContent().equalsIgnoreCase("done")) {
            nextStep = null;
        } else {
        	// try to parse an integer
        	try {
        		int choiceId = Integer.parseInt(e.getMessage().getRawContent());
        		if (choiceId == 1) // time
        			nextStep = new EditTimeStep(messageID);
        		else if (choiceId == 2) // date
        			nextStep = new EditDateStep(messageID);
        		else if (choiceId == 3) // name
        			nextStep = new EditNameStep(messageID);
        		else if (choiceId == 4) // description
        			nextStep = new EditDescriptionStep(messageID);
        		else if (choiceId == 5) // leader
        			nextStep = new EditLeaderStep(messageID);
        		else if (choiceId == 6) // roles
        			nextStep = new EditRoleStep(messageID);
        		else
        			valid = false;
        	} catch (Exception excp) {
        		valid = false;
        	}
        }

    	if (valid == false) {
    		e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Invalid choice. Try again.").queue());
    	}
    	return valid;
    }

    /**
     * {@inheritDoc}
     */
    public String getStepText() {
        return "NOTE: You can type *cancel* at any point during this process to stop editing.\n\n"
        		+ "Choose which property you want to change:\n"
        		+ "`1` time \n"
        		+ "`2` date \n"
        		+ "`3` name \n"
        		+ "`4` description \n"
        		+ "`5` leader \n"
        		+ "`6` roles \n"
        		+ "or type *done* when you want to finish editing.";
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
