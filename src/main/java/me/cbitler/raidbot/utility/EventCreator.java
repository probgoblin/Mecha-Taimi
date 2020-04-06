package me.cbitler.raidbot.utility;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.raids.AutoPendingRaid;
import me.cbitler.raidbot.raids.RaidManager;

public class EventCreator implements ExecutableTask {

	String lastEventId;
	String serverId;
	AutoPendingRaid eventTemplate;
	
	public EventCreator(AutoPendingRaid event)
	{
		eventTemplate = event;
		serverId = event.getServerId();
		lastEventId = "";
	}
	
	@Override
	public void execute() {
		RaidBot bot = RaidBot.getInstance();
		
		if (lastEventId.isEmpty() == false)
		{
            RaidManager.deleteRaid(lastEventId, true);
		}
		
		// always take up-to-date channel
		eventTemplate.setAnnouncementChannel(bot.getAutoEventsChannel(serverId));
		
		lastEventId = RaidManager.createRaid(eventTemplate);
				
		System.out.println("lastEventId is" + lastEventId);
	}

}
