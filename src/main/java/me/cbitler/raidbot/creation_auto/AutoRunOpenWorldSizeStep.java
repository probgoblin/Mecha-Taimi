package me.cbitler.raidbot.creation_auto;

import me.cbitler.raidbot.raids.AutoPendingRaid;
import me.cbitler.raidbot.raids.RaidRole;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Step for choosing squad size for open world event.
 * @author Franziska Mueller
 */
public class AutoRunOpenWorldSizeStep implements AutoCreationStep {

	AutoPendingRaid event;
	
	public AutoRunOpenWorldSizeStep(AutoPendingRaid event) {
        this.event = event;
    }
	
    /**
     * Handle user input.
     * @param e The direct message event
     * @return True if the user entered a valid choice, false otherwise
     */
    public boolean handleDM(PrivateMessageReceivedEvent e) {
        try {
    		int size = Integer.parseInt(e.getMessage().getRawContent());
            event.getRolesWithNumbers().add(new RaidRole(size, "Participants"));
        	return true;
    	} catch (Exception exp) {
            e.getChannel().sendMessage("Please choose a valid option.").queue();
            return false;
    	}
    }

    /**
     * {@inheritDoc}
     */
    public String getStepText() {
        return "Enter the squad size for the open world event:";
    }

    /**
     * {@inheritDoc}
     */
    public AutoCreationStep getNextStep() {
        return new AutoRunResetTimeStep(event);
    }
    
    /**
     * {@inheritDoc}
     */
	public AutoPendingRaid getEventTemplate() {
		return event;
	}
}
