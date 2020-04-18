package me.cbitler.raidbot.raids;


/**
 * Class to store data about a raid that is being set up as auto event
 * This isn't commented as the method names should be self-explanatory
 * @author Franziska Mueller
 */
public class AutoPendingRaid extends PendingRaid {
	
	int resetHour = 0;
	int resetMin = 0;

	int deleteHour = 0;
	int deleteMin = 0;
	
	public int getResetHour() {
        return resetHour;
    }
	
	public int getResetMinutes() {
        return resetMin;
    }

    public void setResetTime(int hour, int mins) {
        resetHour = hour;
        resetMin = mins;
    }
    
	public int getDeleteHour() {
        return deleteHour;
    }
	
	public int getDeleteMinutes() {
        return deleteMin;
    }

    public void setDeleteTime(int hour, int mins) {
        deleteHour = hour;
        deleteMin = mins;
    }
}
