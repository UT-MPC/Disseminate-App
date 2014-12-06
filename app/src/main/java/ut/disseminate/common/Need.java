package ut.disseminate.common;

import java.io.Serializable;

public class Need implements Serializable {

	public String itemName;
	public Integer bit;
	public Double sum;
	
	public Need(String itemName, Integer bit, Double sum) {
		this.itemName = itemName;
		this.bit = bit;
		this.sum = sum;
	}
}
