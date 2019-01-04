/**
 * @description:
 * @author: fancying
 * @create: 2019-01-03 14:56
 **/
package cn.edu.fudan.tagservice.domain;

public class IgnoreRecord {

    private String uuid;
    private String userId;
    private int level;
    private String type;
    private String repoId;
    private String repoName;

    public IgnoreRecord(String uuid, String userId, int level, String type, String repoId, String repoName) {
        this.uuid = uuid;
        this.userId = userId;
        this.level = level;
        this.type = type;
        this.repoId = repoId;
        this.repoName = repoName;
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

    public void setLevel(int level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}