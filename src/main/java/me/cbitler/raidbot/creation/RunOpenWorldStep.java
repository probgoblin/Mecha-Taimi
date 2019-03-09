package me.cbitler.raidbot.creation;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.raids.PendingRaid;
import me.cbitler.raidbot.raids.RaidRole;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Step for choosing between structued or open world event.
 * @author Franziska Mueller
 */
public class RunOpenWorldStep implements CreationStep {

	CreationStep nextStep;

    /**
     * Handle user input.
     * @param e The direct message event
     * @return True if the user entered a valid choice, false otherwise
     */
    public boolean handleDM(PrivateMessageReceivedEvent e) {
        try {
    		int choiceId = Integer.parseInt(e.getMessage().getRawContent());
    		RaidBot bot = RaidBot.getInstance();
            PendingRaid raid = bot.getPendingRaids().get(e.getAuthor().getId());
            if (raid == null) {
                return false;
            }
    		if (choiceId == 1) { // open world
    			raid.setOpenWorld(true);
                raid.getRolesWithNumbers().add(new RaidRole(50, "Participants"));
        		return true;
    		} else if (choiceId == 2) { // structured
    			nextStep = new RunRoleSetupStep();
        		return true;
    		} else {
    			e.getChannel().sendMessage("Please choose a valid option.").queue();
                return false;
    		}
    	} catch (Exception exp) {
            e.getChannel().sendMessage("Please choose a valid option.").queue();
            return false;
    	}
    }

    /**
     * {@inheritDoc}
     */
    public String getStepText() {
        return "Choose if the event should be open world or structured:\n"
        		+ "`1` **open world:** 1 default role (x50), yes/no sign-up for users\n"
        		+ "`2` **structured:** customizable role setup, class sign-up for users";
    }

    /**
     * {@inheritDoc}
     */
    public CreationStep getNextStep() {
        return nextStep;
    }
}
