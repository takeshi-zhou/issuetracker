/**
 * @description:
 * @author: fancying
 * @create: 2019-01-03 14:56
 **/
package cn.edu.fudan.tagservice.domain;

import java.util.Date;

public class IgnoreRecord {

    private String uuid;
    private String userId;
    private String userName;
    private int level;
    private String type;
    private String tool;
    private String repoId;
    private String repoName;
    private String branch;
    private Date updateTime;

    public IgnoreRecord(){
    }

    public IgnoreRecord(String uuid, String userId, String userName, int level, String type, String tool, String repoId, String repoName, String branch, Date updateTime) {
        this.uuid = uuid;
        this.userId = userId;
        this.userName = userName;
        this.level = level;
        this.type = type;
        this.tool = tool;
        this.repoId = repoId;
        this.repoName = repoName;
        this.branch = branch;
        this.updateTime = updateTime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getLevel() {
        return level;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public String getRepoId() {
        return repoId;
    }

    public void setRepoId(String repoId) {
        this.repoId = repoId;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}