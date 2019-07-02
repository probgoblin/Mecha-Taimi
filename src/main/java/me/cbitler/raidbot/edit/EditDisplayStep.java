package me.cbitler.raidbot.edit;

import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Edit display-short-message flag for the event
 * @author Franziska Mueller
 */
public class EditDisplayStep implements EditStep {

	private String messageID;
	
	public EditDisplayStep(String messageId) {
		this.messageID = messageId;
	}
	
    /**
     * Handle changing the display-short-message flag for the event
     * @param e The direct message event
     * @return True if the display-short-message flag is set, false otherwise
     */
    public boolean handleDM(PrivateMessageReceivedEvent e) {
    	boolean valid = true;
    	// try to parse an integer
    	int choiceID = 0;
    	try {
    		choiceID = Integer.parseInt(e.getMessage().getRawContent());
    		if (choiceID < 0 || choiceID >= 2)
    			valid = false;
    	} catch (Exception excp) {
    		valid = false;
    	}
    	if (valid == false) {
    		e.getChannel().sendMessage("Please choose a valid option.").queue();
    		return false;
    	}
    	
    	Raid raid = RaidManager.getRaid(messageID);
        raid.setDisplayShort(choiceID == 1);
        if (raid.updateDisplayShortDB()) {
        	e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Display format successfully updated in database.").queue());
        } else {
        	e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Display format could not be updated in database.").queue());	
        }
        raid.updateMessage();

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String getStepText() {
    	return "Should the event message be displayed in a short format? \n"
        		+ "`0` no \n"
        		+ "`1` yes";
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
