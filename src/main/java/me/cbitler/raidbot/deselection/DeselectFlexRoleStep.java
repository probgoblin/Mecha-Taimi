package me.cbitler.raidbot.deselection;

import java.util.ArrayList;

import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidUser;
import me.cbitler.raidbot.utility.Reactions;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.entities.*;

/**
 * Step for removing a registration from a raid
 * @author Franziska Mueller
 */
public class DeselectFlexRoleStep implements DeselectionStep {
    Raid raid;
    User user;
    DeselectionStep nextStep;

    /**
     * Create a new step for role deselection with the specified raid
     * @param raid The raid
     */
    public DeselectFlexRoleStep(Raid raid, User user) {
        this.raid = raid;
        this.user = user;
    }

    /**
     * Handle the user input
     * @param e The private message event
     * @return True if the user chose a valid, not full, role, false otherwise
     */
    @Override
    public boolean handleDM(PrivateMessageReceivedEvent e) {
        ArrayList<RaidUser> rUsers = this.raid.getRaidUsersById(this.user.getId());
        String msg = e.getMessage().getRawContent();
        if(msg.contentEquals("1")){
            // remove all
        } else if(msg.contentEquals((rUsers.size()+2)+"")){
            // cancel
        } else {
            // remove list
            String[] roles = msg.split(",");
            ArrayList<String> removedRaidUsers = new ArrayList<String>();
            for (String role : roles) {
                int roleSelector = Integer.parseInt(role);
                if(roleSelector <= rUsers.size() + 1){
                    RaidUser raidUser = rUsers.get(roleSelector);
                    if(raid.removeUserFromFlexRoles(e.getAuthor().getId(), raidUser.getRole(), raidUser.getSpec())){
                        removedRaidUsers.add("\""+raidUser.getSpec()+", "+raidUser.getRole()+"\"");
                    }
                }
            }
            if(removedRaidUsers.size()>0){
                e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Selected roles have been removed:\n"+String.join("\n", removedRaidUsers)).queue());
                return true;
            }
        }
        // Send new message because there was no valid selection
        e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Invalid selection.\n\n"+buildSelectionText(rUsers)).queue());
        return false;

        /*
    	if (e.getMessage().getRawContent().equalsIgnoreCase("done")) {
    		nextStep = null;
    		return true;
    	} else {
    		// user has flex roles and did not type done
    		String rawMessage = e.getMessage().getRawContent().replaceAll("\\s","");
    		String[] roleClass = rawMessage.split(",");
    		if (roleClass.length > 1 && raid.removeUserFromFlexRoles(e.getAuthor().getId(), roleClass[0], roleClass[1])) {
    			e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Successfully removed from flex role.").queue());
    			if (raid.getUserNumFlexRoles(e.getAuthor().getId()) == 0) {
    	    		e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("You are not signed up for any flex role anymore.").queue());
    	    		nextStep = null;
    	    		return true;
    	    	}
    			else {
    				e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Specify another [role],[class] to remove or write done.").queue());
    			}
    		} else {
    			e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Please specify a valid role and class for which you are signed up (format: [role],[class] e.g. DPS, Weaver).").queue());
    		}
    		return false;
        }
        */
    }

    /**
     * Get the next step - no next step here as this is a one step process
     * @return null
     */
    @Override
    public DeselectionStep getNextStep() {
        return nextStep;
    }

    private String buildSelectionText(ArrayList<RaidUser> raidUsers){
        int counter = 0;
        String outer = "";
        outer += "`"+(++counter)+"` all\n";
        for (RaidUser rUser : raidUsers) {
            Emote userEmote = Reactions.getEmoteByName(rUser.getSpec());
            if(userEmote!=null){
                outer += "`"+(++counter)+"` <:"+userEmote.getName()+":"+userEmote.getId()+"> "+rUser.getSpec()+", "+rUser.getRole()+"\n";
            }else{
                outer += "`"+(++counter)+"`     "+rUser.getSpec()+", "+rUser.getRole()+"\n";
            }
        }
        outer += "`"+(++counter)+"` cancel";
        return "Choose the specification you want to remove from sign-ups:\n"+outer+"\n"+
                        "alternatively to repeating this step, you may specify a comma-separated list (e.g. 2,3,4) that should not include `all` or `cancel`.";
    }
    /**
     * The step text changes the text based on the available roles.
     * @return The step text
     */
    @Override
    public String getStepText() {
        ArrayList<RaidUser> raidUsers = this.raid.getRaidUsersById(this.user.getId());
        return buildSelectionText(raidUsers);
        //return "Specify [role],[class] (e.g. DPS, Weaver) you want to remove or write done to quit deselection.";
    }
}
