package me.cbitler.raidbot.deselection;

import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidUser;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Step for removing a registration from a raid
 * @author Franziska Mueller
 */
public class DeselectFlexRoleStep implements DeselectionStep {
    Raid raid;
    DeselectionStep nextStep;

    /**
     * Create a new step for role deselection with the specified raid 
     * @param raid The raid
     */
    public DeselectFlexRoleStep(Raid raid) {
        this.raid = raid;
    }

    /**
     * Handle the user input 
     * @param e The private message event
     * @return True if the user chose a valid, not full, role, false otherwise
     */
    @Override
    public boolean handleDM(PrivateMessageReceivedEvent e) {
    	if (e.getMessage().getRawContent().equalsIgnoreCase("done")) {
    		nextStep = null;
    		return true;
    	} else {
    		// user has flex roles and did not type done
    		String rawMessage = e.getMessage().getRawContent();
    		String[] roleClass = rawMessage.split(",");
    		if (roleClass.length > 1 && raid.removeUserFromFlexRoles(e.getAuthor().getId(), roleClass[0], roleClass[1])) {
    			e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Successfully removed from flex role.").queue());	
    			if (raid.getUserNumFlexRoles(e.getAuthor().getId()) == 0) {
    	    		e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("You are not signed up for any flex role anymore.").queue());
    	    		nextStep = null;
    	    		return true;
    	    	}
    			else {
    				e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Specify another role, class to remove or write done.").queue());
    			}
    		} else {
    			e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Please specify a valid role and class for which you are signed up.").queue());
    		}
    		return false;    		
    	}
    }

    /**
     * Get the next step - no next step here as this is a one step process
     * @return null
     */
    @Override
    public DeselectionStep getNextStep() {
        return nextStep;
    }

    /**
     * The step text changes the text based on the available roles.
     * @return The step text
     */
    @Override
    public String getStepText() {
        return "Specify role, class you want to remove or write done to quit deselection.";
    }
}
