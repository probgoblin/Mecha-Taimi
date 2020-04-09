package me.cbitler.raidbot.utility;

import me.cbitler.raidbot.raids.AutoPendingRaid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.server_settings.ServerSettings;

public class EventCreator implements ExecutableTask {

	String lastEventId;
	String serverId;
	int idOnServer;
	String taskId;
	AutoPendingRaid eventTemplate;
	
	public EventCreator(AutoPendingRaid event, int id)
	{
		eventTemplate = event;
		serverId = event.getServerId();
		idOnServer = id;
		taskId = serverId + "_" + Integer.toString(idOnServer);
	}
	
	
	@Override
	public String getName() {
		return eventTemplate.getName() + " @ " + eventTemplate.getTime();
	}
	
	
	@Override
	public void execute() {		
		lastEventId = RaidManager.getEventForAutoCreator(taskId);
		if (lastEventId != null)
		{
            RaidManager.deleteRaid(lastEventId, true);
		}
		
		// always take up-to-date channel
		eventTemplate.setAnnouncementChannel(ServerSettings.getAutoEventsChannel(serverId));
		// TODO: change this to the actual date?
		eventTemplate.setDate("today");
		
		RaidManager.createRaid(eventTemplate, taskId);
	}

}
