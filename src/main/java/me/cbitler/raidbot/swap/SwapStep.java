

package me.cbitler.raidbot.swap;

import java.util.ArrayList;

import me.cbitler.raidbot.raids.FlexRole;
import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.utility.Reactions;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.entities.*;

/**
 * Step for swapping main/flex roles in a raid
 * @author Franziska Mueller
 */
public class SwapStep {
    Raid raid;
    User user;

    /**
     * Create a new step for role swapping with the specified raid
     * @param raid The raid
     */
    public SwapStep(Raid raid, User user) {
        this.raid = raid;
        this.user = user;
    }

    /**
     * Handle the user input
     * @param e The private message event
     * @return True if the user chose a valid role, false otherwise
     */
    public boolean handleDM(PrivateMessageReceivedEvent e) {
        ArrayList<FlexRole> rRoles = this.raid.getRaidUsersFlexRolesById(this.user.getId());
        String msg = e.getMessage().getRawContent();
        if(rRoles.size()==0){
            // no roles
            e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("You are not registered for any flex roles.").queue());
            return true;
        } else if(msg.contentEquals("1")){
            // remove all
            ArrayList<String> removedRaidUsers = new ArrayList<String>();
            for (FlexRole rRole : rRoles) {
                if(raid.removeUserFromFlexRoles(e.getAuthor().getId(), rRole.getRole(), rRole.getSpec())){
                    removedRaidUsers.add("\""+rRole.getSpec()+", "+rRole.getRole()+"\"");
                }
            }
            if(removedRaidUsers.size()>0){
                e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Selected roles have been removed:\n"+String.join("\n", removedRaidUsers)).queue());
                return true;
            }
        } else if(msg.contentEquals((rRoles.size()+2)+"")){
            // cancel
            return true;
        } else {
            // remove list
            String[] roles = msg.split(",");
            ArrayList<String> removedRaidUsers = new ArrayList<String>();
            for (String role : roles) {
            	try {
            		int roleSelector = Integer.parseInt(role) - 2;
            		if(roleSelector >= 0 && roleSelector < rRoles.size()){
            			FlexRole raidRole = rRoles.get(roleSelector);
            			if(raid.removeUserFromFlexRoles(e.getAuthor().getId(), raidRole.getRole(), raidRole.getSpec())){
            				removedRaidUsers.add("\""+raidRole.getSpec()+", "+raidRole.getRole()+"\"");
            			}
            		}
            	} catch (Exception excp) { }
            }
            if(removedRaidUsers.size()>0){
                e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Selected flex roles have been removed:\n"+String.join("\n", removedRaidUsers)).queue());
                return true;
            }
        }
        // Send new message because there was no valid selection
        e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Invalid selection.\n\n"+buildSelectionText(rRoles)).queue());
        return false;
    }

    /**
     * Get the next step - no next step here as this is a one step process
     * @return null
     */
    public SwapStep getNextStep() {
        return null;
    }

    private String buildSelectionText(ArrayList<FlexRole> raidUsers){
        int counter = 0;
        String outer = "";
        outer += "`"+(++counter)+"` all\n";
        for (FlexRole rUser : raidUsers) {
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
    public String getStepText() {
        ArrayList<FlexRole> raidUsers = this.raid.getRaidUsersFlexRolesById(this.user.getId());
        return buildSelectionText(raidUsers);
    }
}
