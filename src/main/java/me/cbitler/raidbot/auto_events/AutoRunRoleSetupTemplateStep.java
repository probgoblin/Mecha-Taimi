package me.cbitler.raidbot.auto_events;

import java.util.List;

import me.cbitler.raidbot.utility.RoleTemplates;
import me.cbitler.raidbot.raids.AutoPendingRaid;
import me.cbitler.raidbot.raids.RaidRole;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Role setup step for the event.
 * Gives the user the option to choose a role template or return to manual creation.
 * @author Franziska Mueller
 */
public class AutoRunRoleSetupTemplateStep implements AutoCreationStep {

    AutoCreationStep nextStep;
    List<RaidRole[]> templates;
    List<String> templateNames;
    AutoPendingRaid event;
    
    public AutoRunRoleSetupTemplateStep(AutoPendingRaid event) {
		this.templates = RoleTemplates.getAllTemplates();
		this.templateNames = RoleTemplates.getAllTemplateNames();
		this.event = event;
		nextStep = new AutoRunResetTimeStep(event);
	}

    /**
     * Handle user input.
     * @param e The direct message event
     * @return True if the user made a valid choice, false otherwise
     */
    public boolean handleDM(PrivateMessageReceivedEvent e) {
        try {
            int choiceId = Integer.parseInt(e.getMessage().getRawContent()) - 1;
            if (choiceId == templateNames.size()) { // user chose to add roles manually
                nextStep = new AutoRunRoleSetupManualStep(event);
                return true;
            } else {
            	event.addTemplateRoles(templates.get(choiceId));
                return true;
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
        String message = "Choose from these available role templates or go back to manual role creation: \n";
        for (int i = 0; i < templateNames.size(); i++) {
        	message += "`" + (i+1) + "` " + RoleTemplates.templateToString(templateNames.get(i), templates.get(i)) + "\n";
        }
        
        return message + "`" + (templateNames.size()+1) + "` add roles manually";
    }

    /**
     * {@inheritDoc}
     */
    public AutoCreationStep getNextStep() {
        return nextStep;
    }
    
    /**
     * {@inheritDoc}
     */
	public AutoPendingRaid getEventTemplate() {
		return event;
	}
}
