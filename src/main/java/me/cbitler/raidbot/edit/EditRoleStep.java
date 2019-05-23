package me.cbitler.raidbot.edit;

import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Edit the roles for the event (rename, add, delete, change amount)
 * @author Franziska Mueller
 */
public class EditRoleStep implements EditStep {

    private String messageID;
    private EditStep nextStep;

    public EditRoleStep(String messageId) {
        this.messageID = messageId;
    }

    /**
     * Handle changing roles for the event
     * @param e The direct message event
     * @return True if a role is changed, false otherwise
     */
    public boolean handleDM(PrivateMessageReceivedEvent e) {
        boolean valid = true;
        // try to parse an integer
        try {
            int choiceId = Integer.parseInt(e.getMessage().getRawContent());
            if (choiceId == 1) // add role
                nextStep = new AddRoleStep(messageID);
            else if (choiceId == 2) // remove role
                nextStep = new DeleteRoleStep(messageID);
            else if (choiceId == 3) // rename role
                nextStep = new RenameRoleStep(messageID);
            else if (choiceId == 4) // change amount
                nextStep = new ChangeAmountStep(messageID);
            else if (choiceId == 5) // change flexOnly
                nextStep = new ChangeFlexOnlyStep(messageID);
            else
                valid = false;
        } catch (Exception excp) {
            valid = false;
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
        return "How do you want to change the roles?\n"
                + "`1` add role\n"
                + "`2` delete role\n"
                + "`3` rename role\n"
                + "`4` change amount of a role\n"
                + "`5` set `flex only` flag of a role";
    }

    /**
     * {@inheritDoc}
     */
    public EditStep getNextStep() {
        return nextStep;
    }

    @Override
    public String getMessageID() {
        return messageID;
    }
}
