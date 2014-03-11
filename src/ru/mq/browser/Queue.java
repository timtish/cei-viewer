package ru.mq.browser;

import java.util.Iterator;

import com.ibm.mq.MQException;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;

public class Queue implements Iterable<QueueMessage> {

	private static final int SHARED_BROWSE_OPTIONS = CMQC.MQOO_INQUIRE 
			+ CMQC.MQOO_FAIL_IF_QUIESCING 
			+ CMQC.MQOO_INPUT_SHARED 
			+ CMQC.MQOO_BROWSE;
	
	private MQQueue queue;
		
	public Queue(MQQueueManager qMgr, String queueName) throws MQException {
		queue = qMgr.accessQueue(queueName, SHARED_BROWSE_OPTIONS);
	}
	
	public Iterator<QueueMessage> iterator() {
		try {
			return new QueueIterator<QueueMessage>(queue, QueueMessage.class);
		} catch (MQException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int getCount() throws MQException {
		return queue.getCurrentDepth();
	}
	
	public void close() {
		try {
			queue.close();
		} catch (MQException e) {
			throw new RuntimeException(e);
		}
	}

}
