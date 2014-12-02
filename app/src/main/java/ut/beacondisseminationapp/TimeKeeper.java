package ut.beacondisseminationapp;

/**
 * Created by Venkat on 12/2/14.
 */
public class TimeKeeper
{
    private long elapsedTime;
    private long startTime;
    private boolean isRunning;
    public static final double NANOS_PER_SEC = 1000000000.0;

    public TimeKeeper()
    {  reset();
    }


    public void start()
    {  if (isRunning) return;
        isRunning = true;
        startTime = System.nanoTime();
    }


    public void stop()
    {  if (!isRunning) return;
        isRunning = false;
        long endTime = System.nanoTime();
        elapsedTime = elapsedTime + endTime - startTime;
    }


    public long checkTime()
    {  if (isRunning)
    {  long endTime = System.nanoTime ();
        elapsedTime = elapsedTime + endTime - startTime;
        startTime = endTime;
    }
        return elapsedTime;
    }


    public void reset()
    {  elapsedTime = 0;
        isRunning = false;
    }
}