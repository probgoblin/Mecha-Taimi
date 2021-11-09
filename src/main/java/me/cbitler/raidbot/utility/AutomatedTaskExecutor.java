package me.cbitler.raidbot.utility;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// -----------------------------------------
// adapted from https://stackoverflow.com/questions/20387881/how-to-run-certain-task-every-day-at-a-particular-time-using-scheduledexecutorse
// -----------------------------------------


public class AutomatedTaskExecutor {
    private static final Logger log = LogManager.getLogger(AutomatedTaskExecutor.class);

    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    ExecutableTask task;
    volatile boolean isStopIssued;

    public AutomatedTaskExecutor(ExecutableTask task)
    {
        this.task = task;
    }

    public String getName()
    {
        return task.getName();
    }

    public void startExecution()
    {
        Runnable taskWrapper = new Runnable(){

            @Override
            public void run()
            {
                task.execute();
                startExecution();
            }

        };
        long delay = computeNextDelay(task.getNextTargetHour(), task.getNextTargetMin(), 0);
        log.trace("Next task scheduled in "+Long.toString(delay)+" seconds.");
        executorService.schedule(taskWrapper, delay, TimeUnit.SECONDS);
    }

    private long computeNextDelay(int targetHour, int targetMin, int targetSec)
    {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.systemDefault();
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedNextTarget = zonedNow.withHour(targetHour).withMinute(targetMin).withSecond(targetSec);
        if(zonedNow.compareTo(zonedNextTarget) >= 0)
            zonedNextTarget = zonedNextTarget.plusDays(1);

        Duration duration = Duration.between(zonedNow, zonedNextTarget);
        return duration.getSeconds();
    }

    public void stop()
    {
        // use shutdownNow instead of shutdown because the latter waits for scheduled tasks to be executed (roughly one day usually!)
        // see: https://stackoverflow.com/questions/11520189/difference-between-shutdown-and-shutdownnow-of-executor-service
        executorService.shutdownNow();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS))
            {
                log.error("awaitTermination in AutomatedTaskExecutor timed out.");
            }
        } catch (InterruptedException ex) {
            log.error("awaitTermination in AutomatedTaskExecutor did not succeed.", ex);
        }
    }
}
