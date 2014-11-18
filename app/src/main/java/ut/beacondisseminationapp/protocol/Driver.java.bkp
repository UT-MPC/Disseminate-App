package ut.beacondisseminationapp.protocol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import ut.beacondisseminationapp.protocol.SendSubscription;
import ut.beacondisseminationapp.network.SendWrapper;
import ut.beacondisseminationapp.common.Beacon;
import ut.beacondisseminationapp.common.BitVector;
import ut.beacondisseminationapp.common.Chunk;
import ut.beacondisseminationapp.common.Item;
import ut.beacondisseminationapp.common.Need;
import ut.beacondisseminationapp.common.Subscription;
import ut.beacondisseminationapp.common.Utility;

public class Driver {
	
	public static volatile ConcurrentHashMap<String, Beacon> beacons
	= new ConcurrentHashMap<String, Beacon> (Utility.NUM_HOSTS, 0.75f, 2); // stores host, beacon
	
	public static Beacon myBeacon = null;
	public static boolean stillNeedItems = true; // flag to only keep subscribing if still need items, TODO: refactor to make this better
	
	// TODO: update my beacon whenever my items are updated
	public static volatile ConcurrentHashMap<String, Item> items
	= new ConcurrentHashMap<String, Item>(Utility.NUM_ITEMS_CAP, 0.75f, 2); // stores item name, item
	
	public static LinkedBlockingQueue<DatagramPacket> chunkQueue = new LinkedBlockingQueue<DatagramPacket>();
	public static LinkedBlockingQueue<DatagramPacket> beaconQueue = new LinkedBlockingQueue<DatagramPacket>();
	public static LinkedBlockingQueue<Subscription> subscriptionQueue = new LinkedBlockingQueue<Subscription>();
	
	public static ArrayList<Integer> priorityItemList = new ArrayList<Integer>();
	//public static Item currentItem = null;
	public static ListIterator<Integer> curItemItr = null;
	public static Chunk virtualChunk = null;
	
	public static long delayStart = 0;
	public static String myIp = "";
	public static double virtualRate = 0;

	
	public static DatagramSocket chunkSocket = null;
	public static DatagramSocket beaconSocket = null;
	
	public static long uselessBroadcastChunks = 0;
	public static long pubBytesSent = 0;
	public static long beaconBytesSent = 0;
	
	public static long startTime = 0;
	public static long endTime = 0;
	
	public Driver(String[] interestItems, int len) {
		init(interestItems, len);
	}
	
	public static void init(String [] args, int len) {
		
		// first arg is the 2 digit host id on the subnet
		myIp = "10.11.12."+args[0];
		
		// second arg is the 
		delayStart = Long.parseLong(args[1]);
		
		// third arg is the virtual rate
		virtualRate = Double.parseDouble(args[2]);
		
		// Get the interested items as arguments from the user
		for (int i=3; i<len; ++i) {
			priorityItemList.add(Integer.parseInt(args[i]));
		}
		
		// Get all the items in the config file
		Map<String, Integer> tempStorage = new HashMap<String, Integer>();
		
		try {
			BufferedReader br = new BufferedReader ( new FileReader("items.cvg"));
			String line;
			while ((line=br.readLine())!=null) {
				String [] tokens = line.trim().split("\\s+");
				tempStorage.put(tokens[0], Integer.parseInt(tokens[1]));
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("items.cvg not found");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		// Set my beacon
		myBeacon = new Beacon(myIp, new HashMap<String, BitVector>());
		
		// Go through priority list and update finalMap from tempStorage, as well as final item count
		for (Integer nextItem: priorityItemList) {
			String itemName = "item"+nextItem;
			int chunkSize = tempStorage.get(itemName);
			items.put(itemName, new Item(itemName,chunkSize, new BitVector(0)));
			myBeacon.bvMap.put(itemName, new BitVector(0));
		}
		
		// Set the current item as the first in the priority list
		//currentItem = items.get("item"+priorityItemList.get(0));
		curItemItr = priorityItemList.listIterator(0);
		
		// Set the virtual chunk
		int firstItemIndex = curItemItr.next();
		Item firstItem = items.get("item"+firstItemIndex);
		virtualChunk = new Chunk(firstItem.name, 0, firstItem.chunkSize, "");
		
	}
	
	public static void cleanup() {
		chunkSocket.close();
		beaconSocket.close();
	}
	
	public static long sourceRate(int chunkSize) {
		double sz = (double)chunkSize;
		double rate = (double)virtualRate*1024.0;
		double period = 1000*(sz/rate);
		return (long)period;
	}
	
	public static void processBeacon(DatagramPacket bPack) {
		Beacon newB = (Beacon) Utility.deserialize(bPack.getData(), bPack.getLength());
		beacons.put(newB.hostIp, newB);
		System.out.println("Beacon received!");
	}
	
	public static void processChunk(DatagramPacket cPack) {
		Chunk newC = (Chunk) Utility.deserialize(cPack.getData(), cPack.getLength());
		Item i = items.get(newC.itemId);
		if (i != null) { // check if the item is one we are interested in
			if (i.bv.testBit(newC.chunkId)) { // check to see if we already have it
				(i.discardedBroadcast)++;
			} else {
				i.bv.setBit(newC.chunkId); // set the bit in the bit vector
				//if (i.startTime == 0) {
				//	i.startTime = System.currentTimeMillis();
				//}
				(i.receivedBroadcast)++;
				if (i.isCompleted()) {
					if (i.completedTime == 0) {
						i.completedTime = System.currentTimeMillis();
					}
					System.out.println("Completed an item: "+i.name);
				}
			}
			items.put(newC.itemId, i);
			myBeacon.bvMap.put(newC.itemId, i.bv);
		} else {
			uselessBroadcastChunks++;
		}
		(new Timer()).schedule(new SendSubscription(), 0);
	}
	
	private static void querySignalStrength() {
		try {
			//long st = System.currentTimeMillis();
			ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c",
					"iw dev wlan0 station dump | grep -E 'Station|signal:'");
			Process p = pb.start();
			p.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line=br.readLine()) != null) {
				String [] tokens = line.trim().split("\\s+");
				String ip = Utility.macToIp.get(tokens[1]);
				//System.out.println("Ip: "+ip);
	
				tokens = br.readLine().trim().split("\\s+");
				int rssi = Integer.parseInt(tokens[1]);
				//System.out.println("RSSI: "+rssi);
				
				// set the signal strength of each beacon
				if (rssi != 0) {
					if (Driver.beacons == null) {
						System.out.println("Beacons should be init!!");
						return;
					}
					Beacon b = Driver.beacons.get(ip);
					if (b != null) {
						b.signalStrength = rssi;
						Driver.beacons.put(ip, b);
					} else {
						//System.out.println("Host not beaconing yet...");
					}
				} else {
					//System.out.println("Signal strength reported as 0 dB, not updated...");
				}
			}
			//long et = System.currentTimeMillis();
			//double ts = (double)(et-st)/1000;
			//System.out.println("Proc time: "+ts);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			//System.out.println("Null on get beacon!");
		}
	}
	
	public static void subscribe() {
		
		HashMap<String, HashMap<Integer,Double>> bitNeeds  = new HashMap<String, HashMap<Integer,Double>>();
		
		PriorityQueue<Need> needs = new PriorityQueue<Need>(priorityItemList.size()*(Utility.NUM_CHUNKS/2), new Comparator<Need>() {
			@Override
			public int compare(Need arg0, Need arg1) {
				return -arg0.sum.compareTo(arg1.sum);
			}
		});
		
		// Get signal strength of each beacon
		Driver.querySignalStrength();
		
		double bestRssi = -1;
		Beacon bestBeacon = null;
		
		stillNeedItems = false;
		// Get the bits we need for each item
		
		// TODO: items are assumed to use naming convention of item#!!
		// traverse backwards!
		ListIterator<Integer> itr = priorityItemList.listIterator(priorityItemList.size());
		while(itr.hasPrevious()) {
		//for (Integer nextIndex : priorityItemList) {
			Item curI = items.get("item"+itr.previous());
			//Item curI = items.get("item"+nextIndex);
			if (!(curI.isCompleted())){ // if the item is not already finished
				stillNeedItems = true;
				BitVector myBv = curI.bv;
				HashMap<Integer,Double> needsMap = new HashMap<Integer,Double>();

				for (int j=0; j<Utility.NUM_CHUNKS; ++j) {
					if(!myBv.testBit(j)) { // if we don't have the bit add it to the list
						needsMap.put(j,0.0);
					}
				}
				
				for (Beacon b: beacons.values()) { // get beacon from each host
					BitVector otherHostBv = b.bvMap.get(curI.name); // get the bitVector for the current item
					
					if (otherHostBv != null) { // check to see if the host has the current item at all
						
						double rssi = convertToPower(b.signalStrength); // get the signal strength for that beacon
						if ((rssi>bestRssi) && (myBv.testDiversity(otherHostBv))) { // can only be best beacon if has something new for me
							bestBeacon = b;
							bestRssi = rssi;
						}
						
						for (Integer curNeed : needsMap.keySet()) { // traverse all needed bits to see which the host BitVector has
							if (otherHostBv.testBit(curNeed)) { // if the other host has it, add it 
									Double rssiSum = needsMap.get(curNeed);
									needsMap.put(curNeed, rssiSum+rssi); // add it to the running sum of signal strengths
							}
						}
					}
				}
				for (Integer curNeed : needsMap.keySet()) {
					Double rssiSum = needsMap.get(curNeed);
					if (rssiSum > 0.0) { // for every bit the determined host has, compute 1/sum(signal_strength)
						needsMap.put(curNeed, 1/rssiSum); // invert each sum in order to get risks
						needs.add(new Need(curI.name,curNeed,1/rssiSum));
					}
				}
				bitNeeds.put(curI.name, needsMap);
			}
		}
		
		// check if still need items
		if (!stillNeedItems) {
			//System.out.println("All done no more subscriptions!");
			if (Driver.endTime == 0) {
				Driver.endTime = System.currentTimeMillis();
				System.out.println("End time: "+ Driver.endTime);
			}
			System.out.println("No more items desired, performing some cleanup");
			//HostMain.stopThreads(); TODO
			//HostMain.sourceTimer.cancel(); TODO
			return;
		}
		
		// determine which host to receive from
		if (bestBeacon == null) {
			//System.out.println("No subscription, since no nearby beacons with interesting info");
			// Schedule a subscription after 1 second
			//(new Timer()).schedule(new SendSubscription(), 1000);
			return;
		} else {
			// retrieve the bit with the largest risk that corresponds to the best beacon
			
			/*Need nextNeed = needs.poll();
			while (!bestBeacon.bvMap.get(nextNeed.itemName).testBit(nextNeed.bit)) {
				// keep getting the bit with the most risk, until we find one that the beacon has
				nextNeed = needs.poll();
				if (nextNeed == null) {
					System.out.println("Error! Somehow did not find a new chunk at the host did was identified as having useful info");
					return;
				}
			}*/
			ArrayList<Need> listNeed = new ArrayList<Need>();
			int listMax = 10;
			Need nextNeed = null;
			int cnt = 0;
			while (((nextNeed = needs.poll()) != null) && cnt < listMax) {
				if (bestBeacon.bvMap.get(nextNeed.itemName).testBit(nextNeed.bit)) {
					cnt++;
					listNeed.add(nextNeed);
				}
			}
			
			int select = (Utility.rng.nextInt(listNeed.size()));
			Need finalNeed = listNeed.get(select);
			
			// create a subscription with this beacon
			//Subscription sub = new Subscription (myBeacon.hostIp, nextNeed.itemName, nextNeed.bit);
			Subscription sub = new Subscription (myBeacon.hostIp, finalNeed.itemName, finalNeed.bit);
			sendSubscription(sub, bestBeacon.hostIp);
		}
		
		// TODO: prioritize any item or just first item (???)
	}
	
	public static void sendSubscription(Subscription sub, String dest) {
		Socket s = new Socket();
		try {
			s.connect(new InetSocketAddress(dest, Utility.SUBSCRIPTION_PORT));
			SendWrapper ts = new SendWrapper(s);
			ts.send(sub);
			ts.close();
		} catch (IOException e) {
			System.out.println("Failed to reach destination with subscription, requeuing for another!");
			(new Timer()).schedule(new SendSubscription(), 0);
		}
	}
	
	public static void gracefulFinish() {
		System.out.println("No more items desired, performing some cleanup");
		//HostMain.stopThreads();
		//HostMain.sourceTimer.cancel();
		//protocol.Driver.dump(aggregateResults());
		//System.exit(0);
	}
	
	public static String aggregateResults() {
		String line = "\n";
		String ind = "\t";
		String r = "";
		long totalRecvBroad = 0;
		long totalDiscardBroad = 0;
		long totalRecvVirtual = 0;
		long totalDiscardVirtual = 0;
		long totalItemTime = 0;
		
		r += "Hostname: "+myBeacon.hostIp+line;
		r += "Virtual Rate: "+virtualRate+line;
		r += "Publication Bytes Sent: "+pubBytesSent+line;
		r += "Beacon Bytes Sent: "+beaconBytesSent+line;
		r += "Useless Broadcast Chunks Received: "+uselessBroadcastChunks+line;

		for (String s : items.keySet()) {
			Item i = items.get(s);
			long time = 0;
			if (i.startTime > 0 ) {
				time = i.completedTime - i.startTime;
			}
			totalItemTime += time;
			r += s+line;
			r += ind+"Completion Time (relative): "+time+line;
			r += ind+"Completion Time (absolute): "+i.completedTime+line;
			r += ind+"Received Broadcast Chunks: "+i.receivedBroadcast+line;
			r += ind+"Discarded Broadcast Chunks: "+i.discardedBroadcast+line;
			r += ind+"Received Virtual Chunks: "+i.receivedVirtual+line;
			r += ind+"Discarded Virtual Chunks: "+i.discardedVirtual+line;
			
			totalRecvBroad+=(i.receivedBroadcast*i.chunkSize);
			totalDiscardBroad+=(i.discardedBroadcast*i.chunkSize);
			totalRecvVirtual+=(i.receivedVirtual*i.chunkSize);
			totalDiscardVirtual+=(i.discardedVirtual*i.chunkSize);
		}
		r += "Total Received Broadcast Bytes: "+totalRecvBroad+line;
		r += "Total Discarded Broadcast Bytes: "+totalDiscardBroad+line;
		r += "Total Received Virtual Bytes: "+totalRecvVirtual+line;
		r += "Total Discarded Virtual Bytes: "+totalDiscardVirtual+line;
		r += "Total Time to Receive All Items: "+totalItemTime+line;
		//TODO: need to add the speeds
		//r += "Total Received Broadcast Rate: "+ protocol.Driver.getRate(totalRecvBroad, protocol.Driver.endTime, protocol.Driver.startTime)+ " KBytes/Sec"+line;
		//r += "Total Discarded Broadcast Rate: "+ protocol.Driver.getRate(totalDiscardBroad, protocol.Driver.endTime, protocol.Driver.startTime)+ " KBytes/Sec"+line;
		//r += "Total Received Virtual Rate: "+ protocol.Driver.getRate(totalRecvVirtual, protocol.Driver.endTime, protocol.Driver.startTime)+ " KBytes/Sec"+line;
		//r += "Total Discarded Virtual Rate: "+ protocol.Driver.getRate(totalDiscardVirtual, protocol.Driver.endTime, protocol.Driver.startTime)+ " KBytes/Sec"+line;
		
		return r;
	}
	public static void dump(String results) {
		File f = new File("results0.txt");
		int i = 0;
		while (f.exists()) {
			i++;
			f = new File("results"+i+".txt");
		}
		try {
			FileWriter fw = new FileWriter(f);
			fw.write(results);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static double convertToPower(int dBm) {
		return Math.pow(10.0, ((double)dBm)/10.0);
	}
	
	public static double getRate (long quantity, long te, long ts) {
		long delta = te - ts;
		double rate = (((double)quantity)/1024.0) / (((double)delta)/1000);
		return rate;
	}
}
