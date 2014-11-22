package ut.beacondisseminationapp.protocol;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;

import ut.beacondisseminationapp.Container;
import ut.beacondisseminationapp.common.Beacon;
import ut.beacondisseminationapp.common.Chunk;
import ut.beacondisseminationapp.common.Packet;
import ut.beacondisseminationapp.common.Utility;

public class PacketProcessor implements Runnable {
	
	public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT);
		//byte [] recvBuf = new byte[Utility.BUF_SIZE];
        while (!Thread.interrupted()) {
            //Log.d("PacketProcessor", "Requesting next packet...");
            DatagramPacket receivedPacket = Protocol.mContainer.receive_packet();
            //DatagramPacket recvPack = new DatagramPacket(recvBuf, recvBuf.length);
            byte [] recvBuf = receivedPacket.getData();
            Integer len = receivedPacket.getLength();
            //Log.d("PacketProcessor", "Length of received packet: "+len.toString());
            Packet newData = (Packet) Utility.deserialize(recvBuf, receivedPacket.getLength());
            if (newData.identifier == Packet.Type.BEACON) {
                Protocol.processBeacon((Beacon) newData);
                //Protocol.readyForSelect.set(true);
            } else if (newData.identifier == Packet.Type.CHUNK) {
                Protocol.processChunk((Chunk) newData);
                //Protocol.readyForSelect.set(true);
            } else {
                Log.d("PacketProcessor", "Packet does not match BEACON or CHUNK types!");
            }
            //MobileHost.chunkSocket.receive(recvPack);
            //MobileHost.chunkQueue.put(recvPack);
            //recvChunk = (Chunk) Utility.deserialize
        }
	}
	
}
