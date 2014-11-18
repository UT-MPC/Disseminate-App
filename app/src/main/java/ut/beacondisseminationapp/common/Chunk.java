package ut.beacondisseminationapp.common;

import java.io.Serializable;

public class Chunk extends Packet {
	
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
        this.identifier = Type.CHUNK;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Chunk)) return false;

        Chunk chunk = (Chunk) o;

        if (chunkId != chunk.chunkId) return false;
        if (!itemId.equals(chunk.itemId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = itemId.hashCode();
        result = 31 * result + chunkId;
        return result;
    }
}
