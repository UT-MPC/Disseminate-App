package ut.beacondisseminationapp.mock;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import ut.beacondisseminationapp.common.Chunk;

/**
 * Created by Venkat on 11/26/14.
 * Static class that simulates a 3G downloader
 * Uncompleted version
 */


public class Sim3G {
    ArrayList<Chunk> serverData = new ArrayList<Chunk>();
    ArrayList<Chunk> downloadedData = new ArrayList<Chunk>();

    public static void init(int chunksPerSecond){
        DownloadSimulator myTask = new DownloadSimulator();
        Timer myTimer = new Timer();
        myTimer.schedule(myTask, 3000, 1500);

    }

    class DownloadSimulator extends TimerTask {
        public void run() {


            System.out.println("");
        }
    }


}

}
