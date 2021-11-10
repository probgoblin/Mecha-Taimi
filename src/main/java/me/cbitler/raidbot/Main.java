package me.cbitler.raidbot;

import javax.security.auth.login.LoginException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.cbitler.raidbot.utility.EnvVariables;

/**
 * Start the program, read the token, and start the bot
 * @author Christopher Bitler
 */
public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws LoginException, InterruptedException {
        String token = EnvVariables.getValue("DISCORD_TOKEN");
        if (token == null || token == "") {
            log.error("Invalid tokenDiscord Bot Token is missing. Please refer to the manual for more information.");
            System.exit(1);
        }

        new RaidBot(token);
    }
}
