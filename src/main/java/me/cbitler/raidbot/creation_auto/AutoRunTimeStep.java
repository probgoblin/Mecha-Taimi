package me.cbitler.raidbot.creation_auto;

import me.cbitler.raidbot.raids.AutoPendingRaid;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Set the time for the event
 * @author Franziska Mueller
 */
public class AutoRunTimeStep implements AutoCreationStep {

	AutoPendingRaid event;
	
	public AutoRunTimeStep(AutoPendingRaid event) {
        this.event = event;
    }
	
    /**
     * Handle setting the time for the event
     * @param e The direct message event
     * @return True if the time is set, false otherwise
     */
    public boolean handleDM(PrivateMessageReceivedEvent e) {
        event.setTime(e.getMessage().getRawContent());

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String getStepText() {
        return "Enter the time for the event:";
    }

    /**
     * {@inheritDoc}
     */
    public AutoCreationStep getNextStep() {
        return new AutoRunOpenWorldStep(event);
    }
    
    /**
     * {@inheritDoc}
     */
	public AutoPendingRaid getEventTemplate() {
		return event;
	}
}
