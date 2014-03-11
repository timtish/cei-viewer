package ru.mq.browser;

import java.awt.HeadlessException;
import java.util.HashMap;
import java.util.Map;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;

public class MQBrowser {

	private MQQueueManager qMgr;

	private Map<String, Queue> queues = new HashMap<String, Queue>();
	
	public MQBrowser(String host, int port, String qmName, String channelName) throws HeadlessException {
		MQEnvironment.hostname = host;
		MQEnvironment.port = port;
		MQEnvironment.channel = channelName;
	    try {
			qMgr = new MQQueueManager(qmName);
		} catch (MQException e) {
			e.printStackTrace();
		}
	}

	public void close() {
        try {
        	for (Queue queue : queues.values()) queue.close();
			qMgr.disconnect();
		} catch (MQException e) {
			e.printStackTrace();
		}
	}

	public Queue getQueue(String queueName) throws MQException {
		Queue queue = queues.get(queueName);
		if (queue == null) {
			queue = new Queue(qMgr, queueName);
			queues.put(queueName, queue);
		}
		return queue;
	}

}
