package ut.beacondisseminationapp.protocol;

import android.util.Log;

import java.net.DatagramPacket;
import java.util.TimerTask;

import ut.beacondisseminationapp.common.Beacon;
import ut.beacondisseminationapp.common.Chunk;
import ut.beacondisseminationapp.common.Packet;
import ut.beacondisseminationapp.common.Utility;

public class PacketBroadcaster implements Runnable {

	@Override
	public void run() {
		//System.out.println("Preparing subscription!");
		//Driver.subscribe();
        //android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        while (!Thread.interrupted()) {
            if (Protocol.readyForSelect.get()) {
                Chunk chunkToSend = Protocol.selectChunk();

                if (chunkToSend != null) {
                    Log.d("PacketBroadcaster", "Broadcasting: (" + chunkToSend.itemId + "," + chunkToSend.chunkId + ")");
                    byte[] chunkBuf = Utility.serialize(chunkToSend, Utility.BUF_SIZE);
                    DatagramPacket chunkPacket = new DatagramPacket(chunkBuf, chunkBuf.length, Utility.broadcastAddr, Utility.RECEIVER_PORT);
                    Log.d("PacketBroadcaster", "Length of chunkPacket: " + chunkBuf.length);
                    Protocol.mContainer.broadcast_packet(chunkPacket);
                    //MobileHost.chunkSocket.receive(recvPack);
                    //MobileHost.chunkQueue.put(recvPack);
                    //recvChunk = (Chunk) Utility.deserialize
                } else { // wait for a state change;
                    Log.d("PacketBroadcaster", "selectChunk did not find anything to send, waiting for state change");
                    Protocol.readyForSelect.set(false);
                }
            }
        }
	}
	
}
