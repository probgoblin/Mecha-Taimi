package me.cbitler.raidbot.server_settings;

import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

import me.cbitler.raidbot.raids.RaidRole;

/**
 * Add a role template
 * @author Franziska Mueller
 */
public class RoleTemplatesAddStep implements RoleTemplatesEditStep {

    private String serverId;
	private List<String> availableRoleTemplates;

    public RoleTemplatesAddStep(String serverId, List<String> availableRoleTemplates) {
        this.serverId = serverId;
        this.availableRoleTemplates = availableRoleTemplates;
    }

    /**
     * Handle adding a role template
     * @param e The direct message event
     * @return True if a role is added, false otherwise
     */
    public boolean handleDM(PrivateMessageReceivedEvent e) {
        boolean valid = true;

        String[] parts = e.getMessage().getContentRaw().split(":");
        if(parts.length != 2)
        {
        	e.getChannel().sendMessage("Invalid input: Make sure it's in the correct format. Did you forget the colon (:)?").queue();
            return false;
        }

        String templateName = parts[0].trim();
        if (availableRoleTemplates.contains(templateName))
        {
        	e.getChannel().sendMessage("This server already has a role template with this name. Choose a different name or delete the existing template first.").queue();
            return false;
        }

        List<RaidRole> roleNames = new ArrayList<>();

        try {
        	boolean success = true;
           	String[] roles = parts[1].trim().split("\\s*/\\s*"); // split with delimiter /
           	for (int r = 0; r < roles.length; r++)
           	{
           		String[] nameAmount = roles[r].trim().split(",");
           		if (nameAmount.length != 2)
           		{
           			success = false;
           			break;
           		}
           		roleNames.add(new RaidRole(Integer.parseInt(nameAmount[1].trim()), nameAmount[0].trim()));
           	}
            
            if (success == false)
            {
            	e.getChannel().sendMessage("Invalid input: Could not parse the list of roles. Make sure it's in the correct format.").queue();
                valid = false;
            }
            else
            {
            	ServerSettings.addRoleTemplate(serverId, templateName, roleNames);
            	e.getChannel().sendMessage("Added template `"+templateName+"` which corresponds to "+ServerSettings.templateToString(null,roleNames)+".").queue();
            }
        } catch (Exception ex) {
            e.getChannel().sendMessage("Invalid input: Could not parse the list of roles. Make sure it's in the correct format.").queue();
            valid = false;
        }

        return valid;
    }

    /**
     * {@inheritDoc}
     */
    public String getStepText() {
        return "Enter a new role template for the server\n"
        		+ "__format:__ `[template name]: [role 1],[amount 1] / [role 2],[amount 2] / ... / [role n],[amount n]`, e.g., `Standard Raid: Tank,1 / ... / DPS,5`\n"
        		+ "(Note: You cannot use colon `:`, semi-colon `;`, slash `/`, or comma `,` in your template or role names!)\n";
    }

    /**
     * {@inheritDoc}
     */
    public RoleTemplatesEditStep getNextStep() {
        return new RoleTemplatesIdleStep(serverId);
    }

	@Override
	public String getServerID() {
		return serverId;
	}
}
