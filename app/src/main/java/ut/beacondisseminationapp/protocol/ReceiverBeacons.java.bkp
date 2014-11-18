
package ut.beacondisseminationapp.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.LinkedBlockingQueue;

import ut.beacondisseminationapp.common.Chunk;
import ut.beacondisseminationapp.common.Utility;

public class ReceiverBeacons implements Runnable {

	// Thread for receiving publications
	
	public ReceiverBeacons() {
	}
	
	public void run() {
		byte [] recvBuf = new byte[Utility.BEACON_SIZE];
		try {
			while (!Thread.interrupted()) {
				DatagramPacket recvPack = new DatagramPacket(recvBuf, recvBuf.length);
				Driver.beaconSocket.receive(recvPack);
				Driver.beaconQueue.put(recvPack);
				//recvChunk = (Chunk) Utility.deserialize
			}
		} catch (IOException e) {
			System.out.println("Beacon Socket disconnected.");
		} catch (InterruptedException e) {
			System.out.println("ReceiverBeacons Thread was interrupted.");
		}
	}
	
}

