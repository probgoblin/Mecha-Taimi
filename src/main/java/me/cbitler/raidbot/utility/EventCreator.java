package me.cbitler.raidbot.utility;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import me.cbitler.raidbot.raids.AutoPendingRaid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.server_settings.ServerSettings;

public class EventCreator implements ExecutableTask {

	String lastEventId;
	String serverId;
	int idOnServer;
	String taskId;
	AutoPendingRaid eventTemplate;
	boolean hasSeparateDelete;
	boolean isCreateNext; // whether creation is next (or deletion), only used if hasSeparateDelete == true
	
	public EventCreator(AutoPendingRaid event, int id)
	{
		eventTemplate = event;
		serverId = event.getServerId();
		idOnServer = id;
		taskId = serverId + "_" + Integer.toString(idOnServer);
        hasSeparateDelete = eventTemplate.getResetHour() != eventTemplate.getDeleteHour()
        		|| eventTemplate.getResetMinutes() != eventTemplate.getDeleteMinutes();
        isCreateNext = true;
	}
	
	
	@Override
	public String getName() {
		return eventTemplate.getName() + " @ " + eventTemplate.getTime();
	}
	
	
	private void deleteLastEvent() {
		lastEventId = RaidManager.getEventForAutoCreator(taskId);
		if (lastEventId != null)
		{
            RaidManager.deleteRaid(lastEventId, true);
		}
	}
	
	
	private void createNextEvent() {
		// always take up-to-date channel
		eventTemplate.setAnnouncementChannel(ServerSettings.getAutoEventsChannel(serverId));
		
		// get the actual date, assuming the post is on the same day
		LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.systemDefault();
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy");
		formatter = formatter.withLocale(Locale.ENGLISH);
		String dateString = zonedNow.format(formatter);
		
		eventTemplate.setDate(dateString);
		
		RaidManager.createRaid(eventTemplate, taskId);
	}
	
	
	@Override
	public void execute() {		
		
		if (hasSeparateDelete == false || isCreateNext == false)
			deleteLastEvent();
		if (hasSeparateDelete == false || isCreateNext == true)
			createNextEvent();
		
		// toggle isCreateNext
		isCreateNext = !isCreateNext;
	}


	@Override
	public int getNextTargetHour() {
		if (hasSeparateDelete)
			return isCreateNext ? eventTemplate.getResetHour() : eventTemplate.getDeleteHour();
		else
			return eventTemplate.getResetHour();
	}


	@Override
	public int getNextTargetMin() {
		if (hasSeparateDelete)
			return isCreateNext ? eventTemplate.getResetMinutes() : eventTemplate.getDeleteMinutes();
		else
			return eventTemplate.getResetMinutes();
	}

}
