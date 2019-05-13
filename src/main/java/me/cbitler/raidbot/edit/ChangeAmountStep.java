package me.cbitler.raidbot.edit;

import java.util.List;

import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.raids.RaidRole;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Change the amount for a role of the event
 * @author Franziska Mueller
 */
public class ChangeAmountStep implements EditStep {

	private String messageID;
	private int roleID;
	
	public ChangeAmountStep(String messageId) {
		this.messageID = messageId;
		this.roleID = -1;
	}
	
    /**
     * Handle changing the amount for a role of the event
     * @param e The direct message event
     * @return True if a amount is changed, false otherwise
     */
    public boolean handleDM(PrivateMessageReceivedEvent e) {
    	boolean finished;
    	
    	int inputNumber;
    	// try to parse an integer
		try {
			inputNumber = Integer.parseInt(e.getMessage().getRawContent());
		} catch (Exception excp) {
			e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Invalid input. Try again.").queue());
			return false;
		}
		
    	Raid raid = RaidManager.getRaid(messageID);
    	List<RaidRole> roles = raid.getRoles();
    	if (roleID == -1) // no role chosen yet
    	{
    		finished = false;
    		inputNumber -= 1;
    		if (inputNumber >= 0 && inputNumber < roles.size()) {
    			roleID = inputNumber;
    			e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Enter new amount for the role *" + roles.get(roleID).getName() + "*:").queue());	    	        
    		} else
        		e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Invalid input. Try again.").queue());	
    	}
    	else // message contains new amount
    	{
    		finished = true; // we are done after we try to add
    		int out = raid.changeAmountRole(roleID, inputNumber);
    		if (out == 0) { // success
    			e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Successfully changed amount.").queue());
    			raid.updateMessage();
    		} else if (out == 1)
    			e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Amount could not be changed, number of users > new amount.").queue());	
    		else if (out == 2)
    			e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Amount could not be changed in database.").queue());		
    	}

    	return finished;
    }

    /**
     * {@inheritDoc}
     */
    public String getStepText() {
        String stepText;
        List<RaidRole> roles = RaidManager.getRaid(messageID).getRoles();
        stepText = "For which role do you want to change the amount? \n";
        for (int r = 0; r < roles.size(); r++)
        	stepText += "`" + (r+1) + "` " + roles.get(r).getName() + " \n";
        
        return stepText;
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
