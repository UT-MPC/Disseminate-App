package ut.beacondisseminationapp.common;

import java.io.Serializable;

public class Item implements Serializable {
	public String name;
	//public int size;
	public int chunkSize;
	public BitVector bv;

	public long startTime = 0;
	public long completedTime = 0;
	
	public long receivedBroadcast = 0;
	public long discardedBroadcast = 0;
	
	public long receivedVirtual = 0;
	public long discardedVirtual = 0;
	
	public Item (String name, int chunkSize, BitVector bv) {
		this.name = name;
		this.chunkSize = chunkSize;
		//this.size = size;
		this.bv = bv;
	}
	
	public boolean isCompleted() {
		if (this.bv.data == -1L) {
			return true;
		} else {
			return false;
		}
	}

	public static String getMessageType(String pack) {
		String [] tokens = pack.split(",");
		return tokens[0];
	}
	
	public static String getItemId(String pack) {
		String [] tokens = pack.split(",");
		return tokens[1];
	}
	
	public static int getChunkId(String pack) {
		String [] tokens = pack.split(",");
		return Integer.parseInt(tokens[2]);
	}
}
