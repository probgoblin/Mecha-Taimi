package me.cbitler.raidbot.creation;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.raids.PendingRaid;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Step to set the display-short-message flag of the event
 * @author Franziska Mueller
 */
public class RunDisplayStep implements CreationStep {

    /**
     * Handle choosing a display format for the event
     * @param e The direct message event
     * @return True if the disaply-short-message flag was set, false otherwise
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
        RaidBot bot = RaidBot.getInstance();
        PendingRaid raid = bot.getPendingRaids().get(e.getAuthor().getId());
        if (raid == null) {
        	// this will be caught in the handler
        	throw new RuntimeException();
        }
        raid.setDisplayShort(choiceID == 1);

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
    public CreationStep getNextStep() {
        return new RunDateStep();
    }
}
