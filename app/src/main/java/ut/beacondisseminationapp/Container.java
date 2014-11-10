package ut.beacondisseminationapp;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Venkat on 11/9/14.
 */
public class Container {

    ArrayBlockingQueue<byte[]> RxFIFO = new ArrayBlockingQueue<byte[]>(1000);  //contains upto 1000 items. store this in the stack
    ArrayBlockingQueue<byte[]> TxFIFO = new ArrayBlockingQueue<byte[]>(1000);  //contains upto 1000 items. store this in the stack

    public Container(){

    }

    //TRANSMIT FUNCTIONS
    public void broadcastUser(byte[] bytesOut){
        TxFIFO.add(bytesOut);
    }  //
    public boolean isTXEmptySystem(){
        return TxFIFO.isEmpty();
    }

    //RX. FUNCTIONS
    public void putIntoRXSystem(byte[] bytesIn){   //USER SHOULD NOT CALL THIS FUNCTION. CALLED BY THE LAYER.
        RxFIFO.add(bytesIn);
    }
    public byte[] getNextBroadcastSystem(){
        while(true) {
            try {
                return TxFIFO.take();
            } catch (InterruptedException e) {
                continue;  //continue trying till you get the item and return it
            }
        }
    }

    public boolean isRXDoneUser(){    //function to call to check if there's anymore items in the receive queue
        return RxFIFO.isEmpty();
    }
    public int getRxSizeUserSystem() {return RxFIFO.size();}
}
