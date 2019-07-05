package me.cbitler.raidbot.utility;

import java.util.ArrayList;
import java.util.List;

import me.cbitler.raidbot.raids.RaidRole;

public class RoleTemplates {
	
	static RaidRole[][] raidTemplates =
	    { 	{	new RaidRole(1, "Tank"),
	            new RaidRole(1, "Supporter"),
	            new RaidRole(2, "Healer"),
	            new RaidRole(1, "BS"),
	            new RaidRole(5, "DPS")
	        }
	    };
	
	static String[] raidTemplateNames = {
	            "default raid"
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
}
