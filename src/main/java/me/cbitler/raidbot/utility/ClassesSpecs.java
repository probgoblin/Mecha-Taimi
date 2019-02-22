package me.cbitler.raidbot.utility;

import me.cbitler.raidbot.RaidBot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ClassesSpecs {
    /**
     * List of classes and their specializations
     */
    static String[][] classesSpecs = { 
    		{	"Guardian",
    			"Dragonhunter", // 547490713764364329
            	"Firebrand"	}, // 547490713928204308
    		{	"Revenant",
            	"Herald", // 547490714049708032
            	"Renegade" }, // 547490713584140323
    		{	"Warrior",
            	"Berserker", // 547490714041450496
            	"Spellbreaker" }, // 547490713990987806
    		{	"Engineer",
            	"Scrapper", // 547490714079068184
            	"Holosmith" }, // 547490713911296040
    		{	"Ranger",
            	"Druid", // 547490713894518792
            	"Soulbeast" }, // 547490714041319439
    		{	"Thief",
            	"Daredevil", // 547490713927942169
            	"Deadeye" }, // 547490714209222676
    		{ 	"Elementalist", 
            	"Weaver", // 547490714007764994
            	"Tempest" }, // 547490714045644810
    		{ 	"Mesmer", 
            	"Chronomancer", // 547490713567232021
            	"Mirage" }, // 547490714624458752
    		{ 	"Necromancer",
            	"Reaper", // 547490713642729478
            	"Scourge" } // 547490713965690897
    };

    /*
    static HashMap<String, Integer> coreClassIds = new HashMap<String, Integer>() {{
        put("Guardian", 0);
        put("Revenant", 1);
        put("Warrior", 2);
        put("Engineer", 3);
        put("Ranger", 4);
        put("Thief", 5);
        put("Elementalist", 6);
        put("Mesmer", 7);
        put("Necromancer", 8);
    }};
    */
    
    static HashMap<String, Integer> coreClassIds = new HashMap<String, Integer>() {{
        put("Dragonhunter", 0);
        put("Herald", 1);
        put("Berserker", 2);
        put("Scrapper", 3);
        put("Druid", 4);
        put("Daredevil", 5);
        put("Tempest", 6);
        put("Chronomancer", 7);
        put("Reaper", 8);
    }};
    
    
    /**
     * Get the array of available specializations for this core class
     * 
     * @param coreclass The core class
     * @return The array of available specializations for this core class
     */
    public static String[] getSpecsForCore(String coreclass) {
    	Integer classId = coreClassIds.get(coreclass);
    	if (classId != null) {
    		return classesSpecs[classId];
    	}
    	else {
    		return new String[0];
    	}
    }
}
