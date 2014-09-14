package ru.mq.viewer;

import java.io.IOException;
import java.util.Collection;

import javax.swing.JApplet;

import org.apache.commons.cli.CommandLine;
import org.w3c.dom.Node;

import ru.mq.browser.MQBrowser;
import ru.mq.browser.Queue;
import ru.mq.browser.QueueMessage;
import ru.mq.browser.QueueMessageCallback;

import com.ibm.mq.MQMessage;
import com.ibm.mq.headers.internal.HexString;

public class CeiViewer extends JApplet {

	private static final long serialVersionUID = -5197749626448911573L;
		
	public static void main(String[] args) throws Exception {
		Options options = new Options();
		options.addParameter("s", "server-address", "server address or host name (default 127.0.0.1)");
		options.addParameter("p", "port", "port number (default 1414)");
		options.addParameter("m", "queue-manager-name", "Queue manager name (default SVR.QM)");
		options.addParameter("c", "channel-name", "Channel name (default SYSTEM.ADMIN.SVRCONN)");
		options.addParameter("q", "queues", "List of queues (if this queues not empty then return error code 1)");
		options.addParameter("e", "events-queues", "List of queues with CEI messages (with parameter -v print messages details)");
		options.addFlag("v", "verbose", "display messages from queues");
		options.addFlag("h", "help", "print help");
		
		CommandLine cmd = options.parse(args);
		
		if (cmd == null || cmd.hasOption("h") || (!cmd.hasOption("q") && !cmd.hasOption("e"))) {
			options.printHelp("CeiViewer -q SERVICE.REQUEST.BACKOUT,DEFAULT.BACKOUT");
			return;
		}
		
		boolean verbose = cmd.hasOption("v");	
		
		// init connection
		String host = cmd.getOptionValue("s", "127.0.0.1");
		int port = Integer.parseInt(cmd.getOptionValue("p", "1414"));
		String queueManager = cmd.getOptionValue("m", "SVR.QM");
		String channelName = cmd.getOptionValue("c", "SYSTEM.ADMIN.SVRCONN");
		
		if (verbose) {
			log("Connect to: " + host + "(" + port + ")" 
					+ ", channel name: " + channelName
					+ ", queue manager: " + queueManager);
		}
		MQBrowser browser = new MQBrowser(host, port, queueManager, channelName);

		// print backout queues information	
		long backoutMessagesCount = 0;
		if (cmd.hasOption("q")) {
			String[] queues = cmd.getOptionValue("q").split(",");
			for (String queueName : queues) {
				Queue queue = browser.getQueue(queueName); 
				long count = queue.getCount();
				log(queueName + ": " + count);
				backoutMessagesCount += queue.getCount();
				if (verbose) {
					for (QueueMessage msg : queue) MESSAGE_PRINT.onMessage(msg);
				}
			}
		}
		
		// print cei events
		if (cmd.hasOption("e")) {
			String[] queues = cmd.getOptionValue("e").split(",");
			for (String queueName : queues) {
				Queue queue = browser.getQueue(queueName); 
				long count = queue.getCount();
				log(queueName + ": " + count);
				if (verbose) {
					for (QueueMessage msg : queue) CEI_PRINT.onMessage(msg);
				}
			}
		}

		browser.close();
		
		if (backoutMessagesCount > 0) {
			System.exit(1); // return error code if backouts not empty 
		}
	}

	private static void log(String s) {
		System.out.println(s);		
	}

	private static final QueueMessageCallback MESSAGE_PRINT = new QueueMessageCallback() {
		public void onMessage(QueueMessage msg) {
			MQMessage mqm = msg.getMessage();
			String messageId = HexString.hexString(msg.getMessage().messageId);
			try {
				log(" message id:" + messageId 
					+ (mqm.backoutCount > 0 ? " repeat:" + mqm.backoutCount : "") 
					+ (mqm.getDataOffset() > 0 ? " offset:" + mqm.getDataOffset() : "") 
					+ " size:" + mqm.getDataLength() + " data:" + msg.getText());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
	
	private static final QueueMessageCallback CEI_PRINT = new QueueMessageCallback() {
		public void onMessage(QueueMessage msg) {
			EventMessage e = new EventMessage(msg.getMessage());
			log(" event " + e.getString("//eventData/eventSequence/@creationTime") + " " +
			    	e.getString("//messageFlowData/messageFlow/@uniqueFlowName") + " " + 
			    	e.getString("//eventData/eventIdentity/@eventName") + " " + 
			    	e.getString("//eventData/eventSequence/@counter") + " " +
					e.getString("//eventData/eventCorrelation/@localTransactionId") + " " + 
			    	e.getString("//messageFlowData/messageFlow/@threadId") + " " +
			    	e.getString("//messageFlowData/node/@nodeLabel") +
					" [" + applicationDataAsString(e) + "]");
		}
	};
	
	private static String applicationDataAsString(EventMessage e) {
		StringBuffer nodeList = new StringBuffer();
		
		Collection<Node> contentData = e.getContent("bitstreamData");
		if (!contentData.isEmpty()) {
			int dataSize = contentData.iterator().next().getFirstChild().getTextContent().length();
			nodeList.append("bitstream(length:" + dataSize + ")");			
		}
		Collection<Node> contentNodes = e.getContent("applicationData", "complexContent");
		
		for (Node node : contentNodes) {
			if (nodeList.length() > 0) nodeList.append(',');
			nodeList.append(EventMessage.nodeToString(node));
		}
		return nodeList.toString();
	}
	
	

}
