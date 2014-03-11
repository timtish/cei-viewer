package ru.mq.browser;

import java.util.Iterator;

import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.constants.CMQC;

public class QueueIterator<T extends QueueMessage> implements Iterator<T> {
	
	private MQQueue queue;
	
	private MQMessage currentMsg;
	
	private MQGetMessageOptions mqGetOptions;
		
	private Class<T> messageType;
		
	public QueueIterator(MQQueue queue, Class<T> messageType) throws MQException {
		this.messageType = messageType;
		this.queue = queue;
		
		mqGetOptions = new MQGetMessageOptions();
		mqGetOptions.options = CMQC.MQGMO_BROWSE_FIRST + CMQC.MQGMO_NO_WAIT;
		currentMsg = queue.getCurrentDepth() > 0 ? readMsg() : null;
		mqGetOptions.options = CMQC.MQGMO_BROWSE_NEXT + CMQC.MQGMO_NO_WAIT;
	}
	
	public boolean hasNext() {
		return currentMsg != null;
	}

	protected MQMessage readMsg() {
		try {
			MQMessage msg = new MQMessage();
			queue.get(msg, mqGetOptions);
			return msg;
		} catch (MQException ex) {
		    if (ex.reasonCode == CMQC.MQRC_NO_MSG_AVAILABLE) {
				return null;
		    } else {
		    	throw new IllegalStateException("A WebSphere MQ Error occured : Completion Code "
		    			+ ex.completionCode + " Reason Code " + ex.reasonCode);
		    }
		}
	}

	public void remove() {
		readMsg();
	}
	
	public void close() {
		try {
			queue.close();
		} catch (MQException e) {
			e.printStackTrace();
		}
	}

	public T next() {
		MQMessage message = currentMsg;
		currentMsg = readMsg();
		try {
			return messageType.getConstructor(MQMessage.class).newInstance(message);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
}

