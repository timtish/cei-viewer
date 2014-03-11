package ru.mq.viewer;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.ibm.mq.MQMessage;

public class EventMessage {

	private Document doc;

	private XPath xPath = XPathFactory.newInstance().newXPath();

	public EventMessage(MQMessage message) {
		try {
			byte[] data = new byte[message.getMessageLength()];
			message.readFully(data);
			doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder()
					.parse(new ByteArrayInputStream(data));
			doc.getDoctype();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public EventMessage(byte[] data) {
		try {
			doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder()
					.parse(new InputSource(new ByteArrayInputStream(data)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Collection<Node> getContent(String... nodeNames) {
		Collection<Node> list = Collections.singletonList(doc.getFirstChild());
		for (String nodeName : nodeNames) {
			list = findNode(list, nodeName);
		}
		return list;
	}

	public Collection<Node> findNode(Collection<Node> list, String nodeName) {
    	Collection<Node> result = new ArrayList<Node>();
    	for (Node e : list) {
    		NodeList nodeList = e.getChildNodes();
    		for (int i = 0; i < nodeList.getLength(); i++) {
    			Node n = nodeList.item(i);
    			if (n.getNodeName().endsWith(nodeName))
    			result.add((Element) n);
    		}
    	}
		return result;
	}

	public String getString(String xPath) {
		try {
			return this.xPath.compile(xPath).evaluate(doc);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String xmlText() {
		return nodeToString(doc);
	}

	public static String nodeToString(Node node) {
		StringWriter sw = new StringWriter();
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.setOutputProperty(OutputKeys.ENCODING, "cp1251");
			t.transform(new DOMSource(node, "cp-1251"), new StreamResult(sw));
		} catch (TransformerException te) {
			System.out.println("nodeToString Transformer Exception");
		}
		return sw.toString();
	}

}
