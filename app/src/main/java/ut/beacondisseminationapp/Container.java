package ut.beacondisseminationapp;

import java.net.DatagramPacket;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Venkat on 11/9/14.
 */
public class Container {

    ArrayBlockingQueue<DatagramPacket> RxFIFO = new ArrayBlockingQueue<DatagramPacket>(1000);  //contains upto 1000 items. store this in the stack
    ArrayBlockingQueue<DatagramPacket> TxFIFO = new ArrayBlockingQueue<DatagramPacket>(1000);  //contains upto 1000 items. store this in the stack

    public Container(){

    }


    //USER FUNCTIONS

    //Gets the next packet that was received by the layer
    public DatagramPacket receive_packet(){
        while(true) {
            try {
                return RxFIFO.take();
            } catch (InterruptedException e) {
                continue;
            }
        }
    }

    //Puts the packet into line to be broadcasted
    public void broadcast_packet(DatagramPacket bytesOut){
        TxFIFO.add(bytesOut);
    }  //user function that broadcasts packet

    public boolean receive_done(){    //function to call to check if there's anymore items in the receive queue
        return RxFIFO.isEmpty();
    }
    public int packet_count() {return RxFIFO.size();}



    //SYSTEM FUNCTIONS, USERS DO NOT CALL THESE!!
    public boolean broadcast_isempty(){
        return TxFIFO.isEmpty();
    }
    public void update_rx(DatagramPacket bytesIn){   //USER SHOULD NOT CALL THIS FUNCTION. CALLED BY THE LAYER.
        RxFIFO.add(bytesIn);
    }
    public DatagramPacket next_txitem(){
        while(true) {
            try {
                return TxFIFO.take();
            } catch (InterruptedException e) {
                continue;  //continue trying till you get the item and return it
            }
        }
    }





}
