package me.cbitler.raidbot.handlers;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.commands.Command;
import me.cbitler.raidbot.commands.CommandRegistry;
import me.cbitler.raidbot.creation.CreationStep;
import me.cbitler.raidbot.creation.RunNameStep;
import me.cbitler.raidbot.edit.EditStep;
import me.cbitler.raidbot.edit.EditIdleStep;
import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.utility.PermissionsUtil;
import me.cbitler.raidbot.utility.RoleTemplates;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Handle channel message-related events sent to the bot
 * @author Christopher Bitler
 * @author Franziska Mueller
 */
public class ChannelMessageHandler extends ListenerAdapter {

    /**
     * Handle receiving a message. This checks to see if it matches the !createEvent or !removeFromEvent commands
     * and acts on them accordingly.
     *
     * @param e The event
     */
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        RaidBot bot = RaidBot.getInstance();
        if (e.getAuthor().isBot()) {
           return;
        }

        if(e.getMessage().getRawContent().startsWith("!")) {
            String[] messageParts = e.getMessage().getRawContent().split(" ");
            String[] arguments = CommandRegistry.getArguments(messageParts);
            Command command = CommandRegistry.getCommand(messageParts[0].replace("!",""));
            if(command != null) {
                command.handleCommand(messageParts[0], arguments, e.getChannel(), e.getAuthor());

                try {
                    e.getMessage().delete().queue();
                } catch (Exception exception) {}
            }
        }
        
        if (PermissionsUtil.hasRaidLeaderRole(e.getMember())) {
            if (e.getMessage().getRawContent().equalsIgnoreCase("!createEvent")) {
            	// check if this user already has an active chat
            	int actvId = bot.userHasActiveChat(e.getAuthor().getId());
    			if (actvId != 0) {
    				RaidBot.writeNotificationActiveChat(e.getAuthor(), actvId);
    				try {
                        e.getMessage().delete().queue();
                    } catch (Exception exception) {}
    				return;
    			}
            	CreationStep runNameStep = new RunNameStep(e.getMessage().getGuild().getId());
            	e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(runNameStep.getStepText()).queue());
            	bot.getCreationMap().put(e.getAuthor().getId(), runNameStep);
            	try {
                    e.getMessage().delete().queue();
                } catch (Exception exception) {}
            } else if (e.getMessage().getRawContent().toLowerCase().startsWith("!removefromevent")) {
                String[] split = e.getMessage().getRawContent().split(" ");
                if(split.length < 3) {
                    e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Format for !removeFromEvent: !removeFromEvent [event id] [name]").queue());
                } else {
                    String messageId = split[1];
                    String name = split[2];

                    Raid raid = RaidManager.getRaid(messageId);

                    if (raid != null && raid.getServerId().equalsIgnoreCase(e.getGuild().getId())) {
                        raid.removeUserByName(name);
                    } else {
                        e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Non-existant event.").queue());
                    }
                }
                try {
                    e.getMessage().delete().queue();
                } catch (Exception exception) {}
            } else if (e.getMessage().getRawContent().toLowerCase().startsWith("!editevent")) {
            	String[] split = e.getMessage().getRawContent().split(" ");
                if(split.length < 2) {
                    e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Format for !editEvent: !editEvent [event id]").queue());
                } else {
                    String messageId = split[1];

                    Raid raid = RaidManager.getRaid(messageId);

                    if (raid != null && raid.getServerId().equalsIgnoreCase(e.getGuild().getId())) {
                    	// check if this user already has an active chat
                    	int actvId = bot.userHasActiveChat(e.getAuthor().getId());
            			if (actvId != 0) {
            				RaidBot.writeNotificationActiveChat(e.getAuthor(), actvId);
            				try {
                                e.getMessage().delete().queue();
                            } catch (Exception exception) {}
            				return;
            			}
                    	// check if the raid is being edited by someone else
                    	if (bot.getEditList().contains(messageId)) {
                    		e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("The selected event is already being edited.").queue());
                    	} else {
                    		// start editing process
                    		EditStep editIdleStep = new EditIdleStep(messageId);
                    		e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(editIdleStep.getStepText()).queue());
                    		bot.getEditMap().put(e.getAuthor().getId(), editIdleStep);
                    		bot.getEditList().add(messageId);
                    	}
                    } else {
                        e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Non-existant event.").queue());
                    }
                }
                try {
                    e.getMessage().delete().queue();
                } catch (Exception exception) {}
            }
        }

        if (e.getMember().getPermissions().contains(Permission.MANAGE_SERVER)) {
        	boolean setEventManager = e.getMessage().getRawContent().toLowerCase().startsWith("!seteventmanagerrole");
        	boolean setFractalCreator = e.getMessage().getRawContent().toLowerCase().startsWith("!setfractalcreatorrole");
        	boolean setFractalChannel = e.getMessage().getRawContent().toLowerCase().startsWith("!setfractalchannel");
            if (setEventManager || setFractalCreator || setFractalChannel) {
                String[] commandParts = e.getMessage().getRawContent().split(" ");
                String specifiedName = combineArguments(commandParts,1);
                if (setEventManager) {
                	RaidBot.getInstance().setRaidLeaderRole(e.getMember().getGuild().getId(), specifiedName);
                	e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Event manager role updated to: " + specifiedName).queue());
                } else if (setFractalCreator) {
                	RaidBot.getInstance().setFractalCreatorRole(e.getMember().getGuild().getId(), specifiedName);
                	e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Fractal creator role updated to: " + specifiedName).queue());
                } else if (setFractalChannel) {
                	RaidBot.getInstance().setFractalChannel(e.getMember().getGuild().getId(), specifiedName);
                	e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Fractal announcement channel updated to: " + specifiedName).queue());
                }
                try {
                    e.getMessage().delete().queue();
                } catch (Exception exception) {}
            }
        }
        
        // new creation command for fractal event
        if (PermissionsUtil.hasRaidLeaderRole(e.getMember()) || PermissionsUtil.hasFractalCreatorRole(e.getMember())) {
        	String createFracCommand = "!createfractal";
            if (e.getMessage().getRawContent().toLowerCase().startsWith(createFracCommand)) {
            	String[] split = e.getMessage().getRawContent().substring(createFracCommand.length()+1).split(";");
            	String helpMessageAccum = "Correct format: !createFractal [name];[date];[time];[team comp id]\n"
            			+ "Available team compositions:\n";
            	for (int t = 0; t < RoleTemplates.getFractalTemplateNames().length; t++) {
            		helpMessageAccum += "`" + (t+1) + "` " + RoleTemplates.getFractalTemplateNames()[t] + "\n";
            	}
            	String helpMessage = helpMessageAccum; // otherwise the lambda for sending the message is unhappy because var not effectively final
                
                if(split.length < 4) {
            		e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Incorrect number of arguments provided.").queue());
                    e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(helpMessage).queue());
                } else { 
                	// check if the team comp is valid
                	boolean validTeamComp = true;
                	int teamCompId = -1;
                	try {
                		teamCompId = Integer.parseInt(e.getMessage().getRawContent()) - 1;
                	} catch (Exception excp) {
                		validTeamComp = false;
                	}
                	if (teamCompId < RoleTemplates.getFractalTemplateNames().length)
                		validTeamComp = false;
                	if (validTeamComp == false) {
                		e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Provided team comp id is invalid.").queue());
                		e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(helpMessage).queue());
                	} else {
                		String name = split[0];
                		String date = split[1];
                		String time = split[2];
                		// create fractal event
                		RaidManager.createFractal(e.getAuthor(), e.getGuild().getId(), name, date, time, teamCompId);
                	}
                }
                try {
                    e.getMessage().delete().queue();
                } catch (Exception exception) {}
            }
        }
    }

    @Override
    public void onGuildMessageDelete(GuildMessageDeleteEvent e) {
        if (RaidManager.getRaid(e.getMessageId()) != null) {
            RaidManager.deleteRaid(e.getMessageId());
        }
    }

    /**
     * Combine the strings in an array of strings
     * @param parts The array of strings
     * @param offset The offset in the array to start at
     * @return The combined string
     */
    private String combineArguments(String[] parts, int offset) {
        String text = "";
        for (int i = offset; i < parts.length; i++) {
            text += (parts[i] + " ");
        }

        return text.trim();
    }
}
