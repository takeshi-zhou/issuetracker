package cn.edu.fudan.eventservice.domain;

import java.util.Date;

/**
 * @author WZY
 * @version 1.0
 **/
public class Event {

    private String id;
    private String category;
    private EventType eventType;
    private String targetType;
    private String targetId;
    private String targetDisplayId;
    private String targetCommitter;
    private String repoId;
    private Date commitTime;

    public Event() {
    }

    public Event(String id, String category, EventType eventType, String targetType, String targetId, String targetDisplayId, String targetCommitter, String repoId, Date commitTime) {
        this.id = id;
        this.category = category;
        this.eventType = eventType;
        this.targetType = targetType;
        this.targetId = targetId;
        this.targetDisplayId = targetDisplayId;
        this.targetCommitter = targetCommitter;
        this.repoId = repoId;
        this.commitTime = commitTime;
    }

    public String getTargetDisplayId() {
        return targetDisplayId;
    }

    public void setTargetDisplayId(String targetDisplayId) {
        this.targetDisplayId = targetDisplayId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetCommitter() {
        return targetCommitter;
    }

    public void setTargetCommitter(String targetCommitter) {
        this.targetCommitter = targetCommitter;
    }

    public String getRepoId() {
        return repoId;
    }

    public void setRepoId(String repoId) {
        this.repoId = repoId;
    }

    public Date getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(Date commitTime) {
        this.commitTime = commitTime;
    }
}


