package me.cbitler.raidbot.utility;

import me.cbitler.raidbot.raids.RaidRole;
import me.cbitler.raidbot.server_settings.ServerSettings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RoleTemplates {

    static RaidRole[][] fractalTemplates =
        { 	{	new RaidRole(1, "Chrono"),
                new RaidRole(1, "Healer"),
                new RaidRole(1, "BS"),
                new RaidRole(2, "DPS")
            },
            {	new RaidRole(1, "Healbrand"),
                new RaidRole(1, "Alacrigade"),
                new RaidRole(1, "BS"),
                new RaidRole(2, "DPS")
            },
            {	new RaidRole(1, "Supporter"),
                new RaidRole(1, "Healer"),
                new RaidRole(1, "BS"),
                new RaidRole(2, "DPS")
            }
        };

    static String[] fractalTemplateNames = {
                "fractal (Chrono)",
                "fractal (Firebrigade)",
                "fractal (general)"
        };

    /**
     * Get fractal templates
     *
     * @return The array of all fractal templates
     */
    public static RaidRole[][] getFractalTemplates() {
        return fractalTemplates;
    }

    /**
     * Get fractal template names
     *
     * @return The array of all fractal template names
     */
    public static String[] getFractalTemplateNames() {
        return fractalTemplateNames;
    }

    /**
     * Converts a template to a string for e.g. printing / displaying
     * @param name The name of the template
     * @param template The template for which a string should be produced
     * @return The string representation of the template
     */
    public static String templateToString(String name, RaidRole[] template) {
        String message = "";
        message += name + " (";
        for (int r = 0; r < template.length; r ++) {
            message += template[r].getAmount() + " x " + template[r].getName();
            if (r != template.length - 1) {
                message += ", ";
            }
        }
        message += ")";
        return message;
    }

    /**
     * Builds the list selection messages as a list of messages to send.
     * @param header The text displayed before the selection list.
     * @param footer The text displayed after the selection list.
     * @return The list of messages (as text) to send.
     */
    public static List<String> buildListText(String header, String footer, List<String> availableRoleTemplates, List<List<RaidRole>> correspondingRoles) {
        List<String> messages = new ArrayList<String>();
        if(header != null && !header.isEmpty()) messages.add(header);

        String currMessage = "";
        Iterator<String> groupsIt = availableRoleTemplates.iterator();
        for (int g = 0; g < availableRoleTemplates.size(); g++) {
            String msg = "`"+groupsIt.next()+"`: " + ServerSettings.templateToString(null, correspondingRoles.get(g)) + "\n";
            if(msg.length() + currMessage.length() > 1800) {
                messages.add(currMessage);
                currMessage = "";
            }
            currMessage += msg;
        }
        messages.add(currMessage);

        if(footer != null && !footer.isEmpty()) messages.add(footer);

        if(messages.size() <= 3) {
            String completeMessage = String.join("\n", messages);
            if(completeMessage.length() < 1900) {
                messages = new ArrayList<String>();
                messages.add(completeMessage);
            }
        }

        return messages;
    }
}
