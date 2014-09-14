package ru.mq.web;

import com.ibm.mq.MQException;
import ru.mq.browser.MQBrowser;
import ru.mq.browser.Queue;

import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by timtish on 14.09.14.
 */
public class QueueStatistic {

    private static final Logger LOG = Logger.getLogger(QueueStatistic.class.getName());

    private Date updateDate;

    private Map<String, Long> count = new HashMap<String, Long>();

    public void loadQueuesInfo(List<String> queueNames) {
        if (queueNames == null || queueNames.isEmpty()) return;

        String host = getProperty("mq.server.address", "127.0.0.1"); // todo: allow many mq instances
        int port = Integer.parseInt(getProperty("mq.server.port", "1414"));
        String queueManager = getProperty("queue.manager.name", "SVR.QM");
        String channelName = getProperty("channel.name", "SYSTEM.ADMIN.SVRCONN");

        MQBrowser browser;
        try {
            browser = new MQBrowser(host, port, queueManager, channelName);
        } catch (MQException e) {
            throw new RuntimeException("Can't connect to " + host + ":" + port + " " + queueManager + "(" + channelName + ")", e);
        }

        try {
            updateDate = new Date();
            // collect information
            for (String queueName : queueNames) try {
                Queue queue = browser.getQueue(queueName);
                count.put(queueName, Long.valueOf(queue.getCount()));
            } catch (MQException e) {
                LOG.warning("Can't read queue " + queueName + ": " + e.getLocalizedMessage());
                count.put(queueName, null);
            }
        } finally {
            browser.close();
        }
    }

    public List<QueueInfo> getQueuesInfo() {
        List<QueueInfo> list = new ArrayList<QueueInfo>(count.size());
        for (Map.Entry<String, Long> q : count.entrySet()) {
            list.add(new QueueInfo(q.getKey(), q.getValue()));
        }
        return list;
    }

    public Date getDate() {
        return updateDate;
    }

    private String getProperty(String key, String defaultValue, String... param) {
        ResourceBundle settings = ResourceBundle.getBundle("settings");
        if (settings.containsKey(key)) {
            return MessageFormat.format(settings.getString(key), param);
        } else {
            return defaultValue;
        }
    }

}
