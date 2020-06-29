package me.cbitler.raidbot.utility;

import java.util.ArrayList;
import java.util.List;

import me.cbitler.raidbot.raids.RaidRole;

public class RoleTemplates {
	
	static RaidRole[][] raidTemplates =
	    { 	
	    	{	new RaidRole(1, "Tank"),
	            new RaidRole(1, "Supporter"),
	            new RaidRole(1, "Druid"),
	            new RaidRole(1, "Off-Healer"),
	            new RaidRole(1, "BS"),
	            new RaidRole(5, "DPS")
	        },
	    	{	new RaidRole(2, "Chronotank"),
	            new RaidRole(1, "Druid"),
	            new RaidRole(1, "Off-Healer"),
	            new RaidRole(1, "BS"),
	            new RaidRole(1, "Epi Scg"),
	            new RaidRole(4, "DPS")
	        },
	    	{	new RaidRole(1, "Chronotank"),
	            new RaidRole(1, "BS (G1)"),
	            new RaidRole(1, "Kiter (G2)"),
	            new RaidRole(1, "Off-Chrono (G3)"),
	            new RaidRole(1, "Druid"),
	            new RaidRole(5, "DPS")
	        }
	    };
	
	static String[] raidTemplateNames = {
	            "default raid",
	            "Desmina",
	            "Dhuum"
	    };
	
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
	
	private static List<RaidRole[]> templates = new ArrayList<RaidRole[]>();
	
	private static List<String> templateNames = new ArrayList<String>();

    /**
     * Get all role templates (merges raid and fractal templates first if not done already)
     *
     * @return The array of all available role templates
     */
    public static List<RaidRole[]> getAllTemplates() {
    	if (templates.isEmpty()) {
    		for (int t = 0; t < raidTemplates.length; t++) 
    			templates.add(raidTemplates[t]);
    		for (int t = 0; t < fractalTemplates.length; t++) 
    			templates.add(fractalTemplates[t]);
    	}
    	return templates;
    }
    
    /**
     * Get all template names (merges raid and fractal template names first if not done already)
     *
     * @return The array of all template names
     */
    public static List<String> getAllTemplateNames() {
    	if (templateNames.isEmpty()) {
    		for (int t = 0; t < raidTemplateNames.length; t++) 
    			templateNames.add(raidTemplateNames[t]);
    		for (int t = 0; t < fractalTemplateNames.length; t++) 
    			templateNames.add(fractalTemplateNames[t]);
    	}
    	return templateNames;
    }
    
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
}
