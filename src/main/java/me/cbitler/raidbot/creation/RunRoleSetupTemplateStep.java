package me.cbitler.raidbot.creation;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.raids.PendingRaid;
import me.cbitler.raidbot.raids.RaidRole;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Role setup step for the event.
 * Gives the user the option to choose a role template or return to manual creation.
 * @author Franziska Mueller
 */
public class RunRoleSetupTemplateStep implements CreationStep {

    CreationStep nextStep;
    private static RaidRole[][] templates =
    { 	{	new RaidRole(1, "Tank"),
            new RaidRole(1, "Supporter"),
            new RaidRole(2, "Healer"),
            new RaidRole(1, "BS"),
            new RaidRole(5, "DPS")
        },
        {	new RaidRole(1, "Chrono"),
            new RaidRole(1, "Healer"),
            new RaidRole(1, "BS"),
            new RaidRole(2, "DPS")
        }
    };
    private static String[] templateNames = {
            "default raid",
            "default fractal"
    };

    /**
     * Handle user input.
     * @param e The direct message event
     * @return True if the user made a valid choice, false otherwise
     */
    public boolean handleDM(PrivateMessageReceivedEvent e) {
        RaidBot bot = RaidBot.getInstance();
        PendingRaid raid = bot.getPendingRaids().get(e.getAuthor().getId());

        try {
            int choiceId = Integer.parseInt(e.getMessage().getRawContent()) - 1;
            if (choiceId == templateNames.length) { // user chose to add roles manually
                nextStep = new RunRoleSetupManualStep();
                return true;
            } else {
                for (int r = 0; r < templates[choiceId].length; r++) {
                    raid.getRolesWithNumbers().add(templates[choiceId][r]);
                }
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

        for (int i = 0; i < templateNames.length; i++) {
            message += "`" + (i+1) + "` " + templateNames[i] + " (";
            for (int r = 0; r < templates[i].length; r ++) {
                message += templates[i][r].getAmount() + " x " + templates[i][r].getName();
                if (r != templates[i].length - 1) {
                    message += ", ";
                }
            }
            message += ") \n";
        }
        return message + "`" + (templateNames.length+1) + "` add roles manually";
    }

    /**
     * {@inheritDoc}
     */
    public CreationStep getNextStep() {
        return nextStep;
    }
}
