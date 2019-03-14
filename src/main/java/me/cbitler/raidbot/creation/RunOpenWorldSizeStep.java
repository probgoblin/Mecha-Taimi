package me.cbitler.raidbot.creation;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.raids.PendingRaid;
import me.cbitler.raidbot.raids.RaidRole;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Step for choosing squad size for open world event.
 * @author Franziska Mueller
 */
public class RunOpenWorldSizeStep implements CreationStep {

    /**
     * Handle user input.
     * @param e The direct message event
     * @return True if the user entered a valid choice, false otherwise
     */
    public boolean handleDM(PrivateMessageReceivedEvent e) {
        try {
    		int size = Integer.parseInt(e.getMessage().getRawContent());
    		RaidBot bot = RaidBot.getInstance();
            PendingRaid raid = bot.getPendingRaids().get(e.getAuthor().getId());
            if (raid == null) {
                return false;
            }
            raid.getRolesWithNumbers().add(new RaidRole(size, "Participants"));
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
    public CreationStep getNextStep() {
        return null;
    }
}
