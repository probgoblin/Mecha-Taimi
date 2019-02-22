package me.cbitler.raidbot.handlers;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.deselection.DeselectIdleStep;
import me.cbitler.raidbot.deselection.DeselectionStep;
import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.selection.PickSpecStep;
import me.cbitler.raidbot.selection.SelectionStep;
import me.cbitler.raidbot.utility.Reactions;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.function.Consumer;

public class ReactionHandler extends ListenerAdapter {
    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        Raid raid = RaidManager.getRaid(e.getMessageId());
        if(e.getUser().isBot()) {
            return;
        }
        if (raid != null) {
        	Emote emote = e.getReactionEmote().getEmote();
            if (emote != null && Reactions.getSpecs().contains(emote.getName())) {
                RaidBot bot = RaidBot.getInstance();
                if (bot.getRoleSelectionMap().get(e.getUser().getId()) == null) {
                	if (!raid.isUserInRaid(e.getUser().getId())) {
                		SelectionStep step = new PickSpecStep(raid, e.getReactionEmote().getEmote().getName(), false);
                		bot.getRoleSelectionMap().put(e.getUser().getId(), step);
                		e.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(step.getStepText()).queue());
                	} else if(raid.getUserNumFlexRoles(e.getUser().getId()) < 2) {
                		SelectionStep step = new PickSpecStep(raid, e.getReactionEmote().getEmote().getName(), true);
                		bot.getRoleSelectionMap().put(e.getUser().getId(), step);
                		e.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(step.getStepText()).queue());
                	} else {
                		e.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("You have selected the maximum number of roles. Press the X reaction to re-select your roles.").queue());        		
                	}	
                } else {
                	e.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("You are already selecting a role.").queue());                	
                }
            } else if(emote != null && emote.getName().equalsIgnoreCase("X_")) {
            	if (raid.isUserInRaid(e.getUser().getId())) {
            		DeselectionStep step = new DeselectIdleStep(raid);
            		RaidBot.getInstance().getRoleDeselectionMap().put(e.getUser().getId(), step);
            		e.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(step.getStepText()).queue());
            	}
            	else {
            		e.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("You are not signed up for this event.").queue());
            	}
            }

            e.getReaction().removeReaction(e.getUser()).queue();
        }
    }

}
