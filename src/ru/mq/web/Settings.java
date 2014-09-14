package ru.mq.web;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by timtish on 14.09.14.
 */
public class Settings {

    private List<String> queues = new ArrayList<String>();

    public List<String> getQueues() {
        return queues;
    }

    public void setQueues(List<String> queues) {
        this.queues = queues;
    }

    public void addQueue(String qname) {
        queues.add(qname);
    }

    public void removeQueue(String qname) {
        queues.add(qname);
    }

    public Settings() {
        loadFromProperties();
    }

    public void loadFromProperties() {
        String queueNames = ResourceBundle.getBundle("settings").getString("monitoring.queues");
        setQueues(Arrays.asList(queueNames.split(",")));
    }

    public void saveToProperties() {
        try {
            Properties settings = new Properties();
            settings.load(getClass().getClassLoader().getResourceAsStream("settings.properties"));
            settings.setProperty("monitoring.queues", queues.toString());
            System.out.println("> " + queues.toString() + ' ' + queues.toArray().toString());
            FileOutputStream file = new FileOutputStream("settings.properties");
            settings.store(file, null);
            file.close();
        } catch (IOException e) {
            throw new RuntimeException("Can't update settings.properties: " + e.getLocalizedMessage(), e);
        }
    }

}
