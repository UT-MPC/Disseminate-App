package ut.beacondisseminationapp.protocol;

import android.app.Activity;
import android.provider.ContactsContract;
import android.util.Log;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import ut.beacondisseminationapp.Container;
import ut.beacondisseminationapp.common.Beacon;
import ut.beacondisseminationapp.common.BitVector;
import ut.beacondisseminationapp.common.Chunk;
import ut.beacondisseminationapp.common.Item;
import ut.beacondisseminationapp.common.Utility;

/**
 * Created by Aurelius on 11/18/14.
 */
public class Protocol {

    public static volatile ConcurrentHashMap<UUID, Beacon> beacons
            = new ConcurrentHashMap<UUID, Beacon> ();
    
    public static volatile ConcurrentHashMap<String, Item> items
            = new ConcurrentHashMap<String, Item> ();
    
    public static AtomicBoolean readyForSelect = new AtomicBoolean(true);
    
    public static Container mContainer;
    
    public static Beacon myBeacon;
    public static UUID myId;
    
    public static Timer beaconTimer;
    //public static String myIp;
    
    private static DisseminationProtocolCallback mProtocolCallback;
    
    
    public static void populateDummyItem() {
        myBeacon.bvMap.put("Item0", new BitVector(-1L));
        Item dummyItem = new Item("Item0", Utility.CHK_SIZE, Utility.NUM_CHUNKS);
        for (int i=0; i<Utility.NUM_CHUNKS; ++i) {
            dummyItem.chunks.put(i, new Chunk("Item0", i, Utility.CHK_SIZE, null));
        }
        items.put("Item0", dummyItem);
    }
    
    // used by UI thread to initialize protocol
    public static void initialize(ArrayList<String> desiredItems, Container packetContainer, Activity parent) {
        
        // attach to UI
        mProtocolCallback = (DisseminationProtocolCallback) parent;
        
        // get the reference to the container
        mContainer = packetContainer;
        
        // create a user id for the device
        myId = UUID.randomUUID();
        
        // intialize the beacon for the device
        myBeacon = new Beacon(myId, new HashMap<String, BitVector>(), null, 0);
        for (String itemId : desiredItems) {
            requestItem(itemId);
        }
        //
        
        Thread packetProcessor = new Thread(new PacketProcessor());
        Thread packetBroadcaster = new Thread(new PacketBroadcaster());
        packetProcessor.start();
        packetBroadcaster.start();
        Timer beaconTimer = new Timer();
        beaconTimer.scheduleAtFixedRate(new BeaconBroadcaster(), 0, 200);
        
    }
    
    // used by UI thread to request items
    public static void requestItem(String itemId) {
        // Add item to beacon
        if (myBeacon.bvMap.get(itemId) == null) {
            myBeacon.bvMap.put(itemId, new BitVector(0));
        }
        // Add item to local list of items
        if (items.get(itemId) == null) {
            items.put(itemId, new Item(itemId, Utility.CHK_SIZE, Utility.NUM_CHUNKS));
        }
    }
    
    
    public static Chunk selectChunk() {
        return randomAlgorithm();
    }
    
    public static Chunk randomAlgorithm() {
        Log.d("randomAlgorithm", "Selecting chunk...");
        HashMap<Chunk, Double> uniquenessMap = new HashMap<Chunk, Double>();
        
        Random rand =  new Random();
         
        for (Item item : items.values()) {
            BitVector myBv = myBeacon.bvMap.get(item.name);
            for (Beacon beacon : beacons.values()){
                //TODO: check timestamp of beacon
                BitVector neighborBv = beacon.bvMap.get(item.name);
                if (neighborBv != null) {
                    BitVector intersection = myBv.oppositeIntersection(neighborBv);
                    int cntFound = 0;
                    for (int i=0; i < Utility.NUM_CHUNKS; ++i) {
                        if (intersection.testBit(i)) {
                            cntFound++;
                            Chunk potentialChunk = item.chunks.get(i);
                            Double uniqueness = uniquenessMap.get(potentialChunk);
                            if (uniqueness != null) {
                                uniquenessMap.put(potentialChunk, uniqueness+1);
                            } else {
                                uniquenessMap.put(potentialChunk, uniqueness);
                            }
                        }
                    }
                    Log.d("randomAlgorithm", "Potenial chunks found: "+cntFound);
                }
            }
        }
        
        Object[] potentialChunks = uniquenessMap.keySet().toArray();
        Chunk selectedChunk;
        if (potentialChunks.length != 0) {
            Log.d("randomAlgorithm", "Selected a chunk...");
            selectedChunk = (Chunk) potentialChunks[rand.nextInt(potentialChunks.length)];
        } else {
            selectedChunk = null;
        }
        return selectedChunk;

    }
    
    public static void processChunk(Chunk newChunk) {
        Log.d("ProcessChunk", "Processing new chunk...");
        if (newChunk != null) {
            Item i = items.get(newChunk.itemId);
            if (i != null) { // check if we are interested in the item
                Log.d("ProcessChunk", "Looking at: ("+newChunk.itemId+","+newChunk.chunkId+")");
                if (i.chunks.get(newChunk.chunkId) == null) { // check to make sure we don't have the chunk
                    Log.d("ProcessChunk", "Datastore updated...");
                    i.chunks.put(newChunk.chunkId, newChunk);
                    Log.d("ProcessChunk", "Completed so far: "+i.chunks.size());
                    BitVector curBv = myBeacon.bvMap.get(newChunk.itemId);
                    curBv.setBit(newChunk.chunkId);
                    mProtocolCallback.chunkComplete(newChunk);
                    if (curBv.isCompleted()) {
                        Log.d("ProcessChunk", "Done with item!");
                        mProtocolCallback.itemComplete(i.name, i.chunks.values().toArray());
                    }
                } else {
                    Log.d("ProcessChunk", "Datastore NOT updated...");
                }
            }
        }
        //TODO: might need to send subscription here
    }
        
    
    public static void processBeacon(Beacon newBeacon) {
        Log.d("ProcessBeacon", "Updating beacon map...");
        if (newBeacon != null) {
            if (!newBeacon.userId.equals(myId)) {
                beacons.put(newBeacon.userId, newBeacon);
                Log.d("ProcessBeacon", "Updated beacon for: " + newBeacon.userId);
            }
        }
    }
    
    public interface DisseminationProtocolCallback {
            public void itemComplete(String itemId, Object[] contents);
            public void chunkComplete(Chunk completedChunk);
        }    
}
