package ut.beacondisseminationapp.protocol;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.TimerTask;
import ut.beacondisseminationapp.common.Utility;

public class BeaconBroadcaster extends TimerTask {
	
	@Override
	public void run() {
        //android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		// Send beacon
		byte [] beaconBuf = Utility.serialize(Protocol.myBeacon, Utility.BUF_SIZE);
		DatagramPacket updatedBeacon = new DatagramPacket(beaconBuf, beaconBuf.length, Utility.broadcastAddr, Utility.RECEIVER_PORT);
		Log.d("BeaconBroadcaster", "Periodic beacon broadcast...");
        Protocol.mContainer.broadcast_packet(updatedBeacon);
		
		//System.out.println("Publication Send Rate: "+Driver.getRate(Driver.beaconBytesSent, System.currentTimeMillis()));
	}
	
}

