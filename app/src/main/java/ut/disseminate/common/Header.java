package ut.disseminate.common;

import java.io.Serializable;

public class Header implements Serializable {
	
	String type;
	
	public Header (String type) {
		this.type = type;
	}
	
}
