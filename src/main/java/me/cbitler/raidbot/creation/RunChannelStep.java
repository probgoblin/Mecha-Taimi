package me.cbitler.raidbot.creation;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.raids.PendingRaid;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Get the announcement channel for the event from the user
 * @author Christopher Bitler
 */
public class RunChannelStep implements CreationStep {
    static String[] defaultChannels = { "board-of-adventures", "gw2-raid-bot", "test" };
	boolean enterManually;
    
	public RunChannelStep() {
		this.enterManually = false;
	}
	
	/**
     * Set the announcement channel
     * @param e The direct message event
     * @return true if the announcement channel was set, false if it was not
     */
    public boolean handleDM(PrivateMessageReceivedEvent e) {
        if (enterManually) {
            String channelWithoutHash = e.getMessage().getRawContent().replace("#","");
        	if (checkAndSetChannel(e.getAuthor().getId(), channelWithoutHash) == false) {
				e.getChannel().sendMessage("Please choose a valid channel.").queue();
				return false;
			}
			return true;
        } else {
        	try {
        		int choiceId = Integer.parseInt(e.getMessage().getRawContent()) - 1;
        		if (choiceId >= 0 && choiceId < defaultChannels.length) { // one of the default channels
        			if (checkAndSetChannel(e.getAuthor().getId(), defaultChannels[choiceId]) == false) {
        				e.getChannel().sendMessage("Please choose a valid channel.").queue();
        				return false;
        			}
        			return true;
        		} else if (choiceId == defaultChannels.length) { // user wants to enter name manually
        			enterManually = true;
        			e.getChannel().sendMessage("Enter the channel for event announcement:").queue();
        			return false;
        		} else { // no valid choice
        			e.getChannel().sendMessage("Please choose a valid option.").queue();
        			return false;
        		}   	
        	} catch (Exception excp) { // not an integer
        		e.getChannel().sendMessage("Please choose a valid option.").queue();
        		return false;
        	}
        }
    }
    
    /** 
     * checks if a given channel exists and if yes, sets this channel as announcement channel
     * @param authorId the user creating the current raid
     * @param channelName the channel name chosen by the user
     * @return true if channel is valid, false otherwise
     */
    private boolean checkAndSetChannel(String authorId, String channelName) {
    	RaidBot bot = RaidBot.getInstance();
    	PendingRaid raid = bot.getPendingRaids().get(authorId);
    	if (raid == null) {
    		// this will be caught in the handler
        	throw new RuntimeException();
    	}
        
    	boolean validChannel = false;
        for (TextChannel channel : bot.getServer(raid.getServerId()).getTextChannels()) {
            if(channel.getName().replace("#","").equalsIgnoreCase(channelName)) {
                validChannel = true;
            }
        }
        
        if (validChannel)
        	raid.setAnnouncementChannel(channelName);

        return validChannel;
    }

    /**
     * {@inheritDoc}
     */
    public String getStepText() {
        String text = "Choose the channel for event announcement:\n";
        for (int c = 0; c < defaultChannels.length; c++)
        	text += "`" + (c+1) + "` *" + defaultChannels[c] + "*\n";
        text += "`" + (defaultChannels.length+1) + "` enter name manually";
        return text;
    }

    /**
     * {@inheritDoc}
     */
    public CreationStep getNextStep() {
        return new RunDisplayStep();
    }
}
