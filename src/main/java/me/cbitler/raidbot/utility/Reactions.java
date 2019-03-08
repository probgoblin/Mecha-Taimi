package me.cbitler.raidbot.utility;

import me.cbitler.raidbot.RaidBot;
import net.dv8tion.jda.core.entities.Emote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reactions {
    /**
     * List of reactions representing classes
     */
    static String[] specs = {
            "Dragonhunter", // 530541439180996638
            "Firebrand", // 530541440300744705
            "Herald", // 530541440086966283
            "Renegade", // 530541439478661120
            "Berserker", // 530541439625592852
            "Spellbreaker", // 530541439675793408
            "Scrapper", // 530541438895652914
            "Holosmith", // 530541438837194753
            "Druid", // 530541439105499136
            "Soulbeast", // 530541439415746585
            "Daredevil", // 530541439705415680
            "Deadeye", // 530541440225509377
            "Weaver", // 530541439239716874
            "Tempest", // 530541439252430858
            "Chronomancer", // 530541439621267456
            "Mirage", // 530541438707040277
            "Reaper", // 530541439038259216
            "Scourge", // 530541439772393484
            "Guardian", // 530541438891589643
            "Revenant", // 530541439751290901
            "Warrior", // 530541440145555466
            "Engineer", // 530541439172608062
            "Ranger", // 530541439642501121
            "Thief", // 530541439701221378
            "Elementalist", // 530541438891589642
            "Mesmer", // 530541438769823747
            "Necromancer" // 530541439218876416
    };

    static Emote[] reactions = {
            getEmoji("530541439180996638"), // Dragonhunter
            getEmoji("530541440300744705"), // Firebrand
            getEmoji("530541440086966283"), // Herald
            getEmoji("530541439478661120"), // Renegade
            getEmoji("530541439625592852"), // Berserker
            getEmoji("530541439675793408"), // Spellbreaker
            getEmoji("530541438895652914"), // Scrapper
            getEmoji("530541438837194753"), // Holosmith
            getEmoji("530541439105499136"), // Druid
            getEmoji("530541439415746585"), // Soulbeast
            getEmoji("530541439705415680"), // Daredevil
            getEmoji("530541440225509377"), // Deadeye
            getEmoji("530541439239716874"), // Weaver
            getEmoji("530541439252430858"), // Tempest
            getEmoji("530541439621267456"), // Chronomancer
            getEmoji("530541438707040277"), // Mirage
            getEmoji("530541439038259216"), // Reaper
            getEmoji("530541439772393484"), // Scourge
            getEmoji("530541438891589643"), // Guardian
            getEmoji("530541439751290901"), // Revenant
            getEmoji("530541440145555466"), // Warrior
            getEmoji("530541439172608062"), // Engineer
            getEmoji("530541439642501121"), // Ranger
            getEmoji("530541439701221378"), // Thief
            getEmoji("530541438891589642"), // Elementalist
            getEmoji("530541438769823747"), // Mesmer
            getEmoji("530541439218876416"), // Necromancer
            getEmoji("548530947591634944") // X_
    };

    static Emote[] reactionsCore = {
            getEmoji("530541438891589643"), // Guardian
            getEmoji("530541439751290901"), // Revenant
            getEmoji("530541440145555466"), // Warrior
            getEmoji("530541439172608062"), // Engineer
            getEmoji("530541439642501121"), // Ranger
            getEmoji("530541439701221378"), // Thief
            getEmoji("530541438891589642"), // Elementalist
            getEmoji("530541438769823747"), // Mesmer
            getEmoji("530541439218876416"), // Necromancer
            getEmoji("548530947591634944") // X_
    };

    static Emote[] reactionsOpenWorld = {
            getEmoji("553296724584562688"), // Check
            getEmoji("548530947591634944")  // X_
    };

    /**
     * Get an emoji from it's emote ID via JDA
     *
     * @param id The ID of the emoji
     * @return The emote object representing that emoji
     */
    private static Emote getEmoji(String id) {
        return RaidBot.getInstance().getJda().getEmoteById(id);
    }

    /**
     * Get the list of reaction names as a list
     *
     * @return The list of reactions as a list
     */
    public static List<String> getSpecs() {
        return new ArrayList<>(Arrays.asList(specs));
    }

    /**
     * Get the list of emote objects
     *
     * @return The emotes
     */
    public static List<Emote> getEmotes() {
        return new ArrayList<>(Arrays.asList(reactions));
    }

    /**
     * Get the list of core class emote objects
     *
     * @return The emotes
     */
    public static List<Emote> getCoreClassEmotes() {
        return new ArrayList<>(Arrays.asList(reactionsCore));
    }

    public static Emote getEmoteByName(String name) {
        for (Emote emote : reactions) {
            if (emote != null && emote.getName().equalsIgnoreCase(name)) {
                return emote;
            }
        }
        return null;
    }
}
