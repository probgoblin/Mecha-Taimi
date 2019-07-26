package me.cbitler.raidbot.commands;

import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.utility.PermissionsUtil;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.List;

public class EndAllCommand implements Command {
    @Override
    public void handleCommand(String command, String[] args, TextChannel channel, User author) {
        Member member = channel.getGuild().getMember(author);
        if (PermissionsUtil.hasRaidLeaderRole(member)) {
            List<Raid> allEvents = RaidManager.getAllRaids();
            int numEvents = allEvents.size();
            List<String> notDeleted = new ArrayList<String>();
            for (int ev = numEvents-1; ev >= 0; ev--) {
            	Raid raid = allEvents.get(ev);
                if (raid != null && raid.getServerId().equalsIgnoreCase(channel.getGuild().getId())) {
                	String name = raid.getName();
                    boolean deleted = RaidManager.deleteRaid(raid.getMessageId());
                    if (deleted == false) {
                    	notDeleted.add(name);
                    }
                }
            }
            author.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Successfully ended " + (numEvents - notDeleted.size()) + " of " + numEvents + " existing events.").queue());
            for (String eventName : notDeleted) {
            	author.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Could not end event: " + eventName).queue());
            }
        }
    }
}
