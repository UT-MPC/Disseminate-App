package ut.beacondisseminationapp.common;

import java.io.Serializable;

public class Chunk implements Serializable {
	
	public String itemId;
	public int chunkId;
	public int size;
	public String destination;
	public byte [] data;
	
	public Chunk (String itemId, int chunkId, int size, String destination) {
		//super("chunk");
		this.itemId = itemId;
		this.chunkId = chunkId;
		this.size = size;
		this.data = new byte [size]; 
	}

}
