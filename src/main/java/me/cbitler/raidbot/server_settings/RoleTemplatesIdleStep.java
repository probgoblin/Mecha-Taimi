package me.cbitler.raidbot.server_settings;

import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import me.cbitler.raidbot.raids.RaidRole;

/**
 * Edit a role template for server
 * @author Franziska Mueller
 */
public class RoleTemplatesIdleStep implements RoleTemplatesEditStep {

	private String serverId;
	private RoleTemplatesEditStep nextStep;
	private Set<String> availableRoleTemplates;
	private List<List<RaidRole>> correspondingRoles;

	public RoleTemplatesIdleStep(String serverId) {
		this.serverId = serverId;
		availableRoleTemplates = ServerSettings.getRoleTemplateNames(serverId);
		correspondingRoles = ServerSettings.getAllRolesForTemplates(serverId);
	}

    /**
     * Idle step in editing process
     * @param e The direct message event
     * @return True if the user passed the name of an editable property
     */
    public boolean handleDM(PrivateMessageReceivedEvent e) {
        boolean valid = true;
        if(e.getMessage().getContentRaw().equalsIgnoreCase("done")) {
            nextStep = null;
        } else {
        	// try to parse an integer
        	try {
        		int choiceId = Integer.parseInt(e.getMessage().getContentRaw());
        		if (choiceId == 1) // add: always possible
        			nextStep = new RoleTemplatesAddStep(serverId, availableRoleTemplates);
        		else if (choiceId == 2) // remove: only if we have at least one
        		{
        			if (availableRoleTemplates.isEmpty())
        			{
        				e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("This server does not have any role templates to remove.").queue());
        				valid = false;
        			}
        			else
        				nextStep = new RoleTemplatesDeleteStep(serverId, availableRoleTemplates, correspondingRoles);
        		}
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
        String message = "**Edit role templates:**\n"
        		+ "NOTE: You can type *cancel* at any point during this process to stop editing.\n\n"
        		+ "Currently available role templates on this server:\n";
        Iterator<String> groupsIt = availableRoleTemplates.iterator();

        for (int g = 0; g < availableRoleTemplates.size(); g++)
        {
        	message += "`"+groupsIt.next()+"`: ";
        	message += ServerSettings.templateToString(null, correspondingRoles.get(g));
        	message += "\n";
        }

        message += "\nChoose what you want to do:\n"
        		+ "`1` add a role template \n"
        		+ "`2` remove a role template \n"
        		+ "or type *done* when you want to finish editing.";
        return message;
    }

    /**
     * {@inheritDoc}
     */
    public RoleTemplatesEditStep getNextStep() {
        return nextStep;
    }

    @Override
	public String getServerID() {
		return serverId;
	}
}
