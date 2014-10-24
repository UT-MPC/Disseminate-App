package ut.beacondisseminationapp.common;

import java.io.Serializable;
import java.util.HashMap;

public class Beacon implements Serializable {

	public String hostIp;
	public int signalStrength = 0;
	public HashMap<String, BitVector> bvMap; // map of itemId to bit vector
	
	public Beacon(String hostIp, HashMap<String, BitVector> bvMap) {
		this.hostIp = hostIp;
		this.bvMap = bvMap;
	}
}
