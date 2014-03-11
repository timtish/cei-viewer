package ru.mq.browser;

import java.io.IOException;

import com.ibm.mq.MQMessage;

public class QueueMessage {

	private MQMessage message;
	
	public QueueMessage(MQMessage message) {
		this.message = message;
	}
	
	public MQMessage getMessage() {
		return message;
	}

	public String getText() {
		try {
			byte[] buffer = new byte[message.getDataLength()];
			message.readFully(buffer);
			return new String(buffer, "utf-8"); 
		} catch (IOException e) {
			throw new RuntimeException(e);
		} 
	}
	
}
