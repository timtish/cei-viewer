package ru.mq.web;

/**
 * Created by timtish on 14.09.14.
 */
public class QueueInfo {

    private String name;

    private Long depth;

    public QueueInfo() {
    }

    public QueueInfo(String name, Long depth) {
        this.name = name;
        this.depth = depth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDepth() {
        return depth;
    }

    public void setDepth(Long depth) {
        this.depth = depth;
    }
}
