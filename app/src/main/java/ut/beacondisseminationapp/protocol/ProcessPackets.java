package ut.beacondisseminationapp.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.LinkedBlockingQueue;

import ut.beacondisseminationapp.common.Chunk;
import ut.beacondisseminationapp.common.Utility;

public class ProcessPackets implements Runnable {
	
	// Thread for processing chunks
	
	public ProcessPackets() {

	}
	
	public void run() {
		try {
			while (!Thread.interrupted()) {
				DatagramPacket pack = Driver.chunkQueue.take();
				Driver.processChunk(pack);
			}
		} catch (InterruptedException e) {
			System.out.println("ProcessPackets Thread was interrupted.");
		}
	}
	
}
