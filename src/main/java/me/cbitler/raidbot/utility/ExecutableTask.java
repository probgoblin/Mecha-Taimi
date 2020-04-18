package me.cbitler.raidbot.utility;

public interface ExecutableTask {
	
	public String getName();
	
	public int getNextTargetHour();
	
	public int getNextTargetMin();
	
	public void execute();

}
