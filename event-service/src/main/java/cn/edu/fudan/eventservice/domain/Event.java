package cn.edu.fudan.eventservice.domain;

import java.util.Date;

/**
 * @author WZY
 * @version 1.0
 **/
public class Event {

    private String id;
    private EventType eventType;
    private String targetType;
    private String targetId;
    private String targetCommitter;
    private String repoId;
    private Date createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
