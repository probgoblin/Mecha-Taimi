package me.cbitler.raidbot.handlers;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.creation.CreationStep;
import me.cbitler.raidbot.creation_auto.AutoCreationStep;
import me.cbitler.raidbot.deselection.DeselectionStep;
import me.cbitler.raidbot.edit.EditStep;
import me.cbitler.raidbot.logs.LogParser;
import me.cbitler.raidbot.raids.AutoPendingRaid;
import me.cbitler.raidbot.raids.PendingRaid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.selection.SelectionStep;
import me.cbitler.raidbot.swap.SwapStep;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Handle direct messages sent to the bot
 * @author Christopher Bitler
 * @author Franziska Mueller
 */
public class DMHandler extends ListenerAdapter {
    RaidBot bot;

    /**
     * Create a new direct message handler with the parent bot
     * @param bot The parent bot
     */
    public DMHandler(RaidBot bot) {
        this.bot = bot;
    }

    /**
     * Handle receiving a private message.
     * This checks to see if the user is currently going through the event creation process or
     * the role selection process and acts accordingly.
     * @param e The private message event
     */
    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        User author = e.getAuthor();

        if (bot.getCreationMap().containsKey(author.getId())) {
            if(e.getMessage().getRawContent().equalsIgnoreCase("cancel")) {
                cancelCreation(author);
                return;
            }

            CreationStep step = bot.getCreationMap().get(author.getId());
            boolean done = false;
            try {
            	done = step.handleDM(e);
            } catch (RuntimeException excp) {
            	e.getChannel().sendMessage("I could not find any pending event associated with you. I'm sorry.").queue();
            	cancelCreation(author);
            	return;
            }

            // If this step is done, move onto the next one or finish
            if (done) {
                CreationStep nextStep = step.getNextStep();
                if(nextStep != null) {
                    bot.getCreationMap().put(author.getId(), nextStep);
                    e.getChannel().sendMessage(nextStep.getStepText()).queue();
                } else {
                    //Create raid
                    bot.getCreationMap().remove(author.getId());
                    PendingRaid raid = bot.getPendingRaids().remove(author.getId());
                    try {
                        RaidManager.createRaid(raid);
                        e.getChannel().sendMessage("Event created.").queue();
                    } catch (Exception exception) {
                        e.getChannel().sendMessage("Cannot create event - does the bot have permission to post in the specified channel?").queue();
                    }
                }
            }
        } else if (bot.getRoleSelectionMap().containsKey(author.getId())) {
            if(e.getMessage().getRawContent().equalsIgnoreCase("cancel")) {
                bot.getRoleSelectionMap().remove(author.getId());
                e.getChannel().sendMessage("Role selection cancelled.").queue();
                return;
            }
            SelectionStep step = bot.getRoleSelectionMap().get(author.getId());
            boolean done = step.handleDM(e);

            //If this step is done, move onto the next one or finish
            if(done) {
                SelectionStep nextStep = step.getNextStep();
                if(nextStep != null) {
                    bot.getRoleSelectionMap().put(author.getId(), nextStep);
                    e.getChannel().sendMessage(nextStep.getStepText()).queue();
                } else {
                    // We don't need to handle adding to the raid here, that's done in the pickrolestep
                    bot.getRoleSelectionMap().remove(author.getId());
                }
            }
        } else if (bot.getEditMap().containsKey(author.getId())) {
        	if(e.getMessage().getRawContent().equalsIgnoreCase("cancel")) {
                cancelEdit(author);
                return;
            }
        	
            EditStep step = bot.getEditMap().get(author.getId());
            boolean done = step.handleDM(e);

            // If this step is done, move onto the next one or finish
            if (done) {
                EditStep nextStep = step.getNextStep();
                if(nextStep != null) {
                    bot.getEditMap().put(author.getId(), nextStep);
                    e.getChannel().sendMessage(nextStep.getStepText()).queue();
                } else {
                    // finish editing
                    String messageId = step.getMessageID();
                    bot.getEditMap().remove(author.getId());
                    bot.getEditList().remove(messageId);
                    e.getChannel().sendMessage("Finished editing event.").queue();
                }
            }
        } else if (bot.getAutoCreationMap().containsKey(author.getId())) {
            if(e.getMessage().getRawContent().equalsIgnoreCase("cancel")) {
                cancelAutoCreation(author);
                return;
            }

            AutoCreationStep step = bot.getAutoCreationMap().get(author.getId());
            boolean done = false;
            try {
            	done = step.handleDM(e);
            } catch (RuntimeException excp) {
            	e.getChannel().sendMessage("Something went wrong. I'm sorry.").queue();
            	cancelAutoCreation(author);
            	return;
            }

            // If this step is done, move onto the next one or finish
            if (done) {
                AutoCreationStep nextStep = step.getNextStep();
                if(nextStep != null) {
                    bot.getAutoCreationMap().put(author.getId(), nextStep);
                    e.getChannel().sendMessage(nextStep.getStepText()).queue();
                } else {
                    // create automated task
                	bot.getAutoCreationMap().remove(author.getId());
                    AutoPendingRaid event = step.getEventTemplate();
                    boolean success = true;
                    try {
                    	success = bot.createAutoEvent(event);   
                    } catch (Exception exception) {
                    	System.out.println("Exception caught: "+exception.getMessage());
                    	success = false;
                    }
                    if (success)
                    	e.getChannel().sendMessage("Automated event successfully created.").queue();
                    else
                    	e.getChannel().sendMessage("Automated event could not be created. Make sure the maximum number of auto events is not reached yet.").queue();
           
                }
            }
        } else if (bot.getRoleDeselectionMap().containsKey(author.getId())) {
        	DeselectionStep step = bot.getRoleDeselectionMap().get(author.getId());
        	boolean done = step.handleDM(e);

        	// If this step is done, move onto the next one or finish
        	if (done) {
        		DeselectionStep nextStep = step.getNextStep();
        		if(nextStep != null) {
        			bot.getRoleDeselectionMap().put(author.getId(), nextStep);
        			e.getChannel().sendMessage(nextStep.getStepText()).queue();
        		} else {
        			// finish deselection
        			bot.getRoleDeselectionMap().remove(author.getId());
        			e.getChannel().sendMessage("Finished deselection.").queue();
        		}
        	}
        } else if (bot.getRoleSwapMap().containsKey(author.getId())) {
        	SwapStep step = bot.getRoleSwapMap().get(author.getId());
        	// the swap step does not have a next step
        	if (step.handleDM(e)) {
        		// finish role swap
    			bot.getRoleSwapMap().remove(author.getId());
    			e.getChannel().sendMessage("Finished role swap.").queue();
        	}
        }

        if(e.getMessage().getAttachments().size() > 0 && e.getChannelType() == ChannelType.PRIVATE) {
            for(Message.Attachment attachment : e.getMessage().getAttachments()) {
                System.out.println(attachment.getFileName());
                if(attachment.getFileName().endsWith(".evtc") || attachment.getFileName().endsWith(".evtc.zip")) {
                    new Thread(new LogParser(e.getChannel(), attachment)).start();
                }
            }
        }
    }
    
    /**
     * cancels event creation, i.e. removes the user from the creation map, removes pending event, and sends notification
     * @param user the user who started the creation process
     */
    private void cancelCreation(User author) {
    	bot.getCreationMap().remove(author.getId());

        if(bot.getPendingRaids().get(author.getId()) != null) {
            bot.getPendingRaids().remove(author.getId());
        }
        author.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Event creation cancelled.").queue());    	
    }
    
    /**
     * cancels event editing, i.e. removes the user from the edit map, removes event from the edit list, and sends notification
     * @param user the user who started the editing process
     */
    private void cancelEdit(User author) {
    	bot.getEditList().remove(bot.getEditMap().get(author.getId()).getMessageID());
        bot.getEditMap().remove(author.getId());
        
        author.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Event editing cancelled.").queue());    	
        
    }
    
    /**
     * cancels auto event creation, i.e. removes the user from the auto creation map and sends notification
     * @param user the user who started the auto creation process
     */
    private void cancelAutoCreation(User author) {
    	bot.getAutoCreationMap().remove(author.getId());

        author.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Creation of automated event cancelled.").queue());    	
    }
}
