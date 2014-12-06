package ut.disseminate.common;

import java.io.Serializable;

public class Message implements Serializable {
	
	public String type;
	
	public Message(String type) {
		this.type=type;
	}
}
