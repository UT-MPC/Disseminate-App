package ut.beacondisseminationapp;

/**
 * Created by Venkat on 11/20/14.
 */

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
    //PRECONDITION: THE SIZE OF DATAIN HAS TO MATCH THE SIZE THE OBJECT WAS INITALIZED WITH.
    public int setData(byte[] datain){
        //returns 1 if successful, 0 if failed
        if(!(data.length == datain.length)){
            return 0;
        }
        for(int i=0; i<datain.length; i++){
            data[i]=datain[i];
        }
        return 1;
    }

}