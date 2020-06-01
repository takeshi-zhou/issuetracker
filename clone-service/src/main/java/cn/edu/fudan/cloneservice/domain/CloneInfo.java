package cn.edu.fudan.cloneservice.domain;

/**
 * @author zyh
 * @date 2020/5/28
 */
public class CloneInfo {

    private String uuid;
    private String repoId;
    private String commitId;
    private String filePath;
    private String newCloneLines;
    private String selfCloneLines;
    private String type;

    public CloneInfo(){

    }

    public CloneInfo(String uuid, String repoId, String commitId, String filePath, String newCloneLines, String selfCloneLines, String type) {
        this.uuid = uuid;
        this.repoId = repoId;
        this.commitId = commitId;
        this.filePath = filePath;
        this.newCloneLines = newCloneLines;
        this.selfCloneLines = selfCloneLines;
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRepoId() {
        return repoId;
    }

    public void setRepoId(String repoId) {
        this.repoId = repoId;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getNewCloneLines() {
        return newCloneLines;
    }

    public void setNewCloneLines(String newCloneLines) {
        this.newCloneLines = newCloneLines;
    }

    public String getSelfCloneLines() {
        return selfCloneLines;
    }

    public void setSelfCloneLines(String selfCloneLines) {
        this.selfCloneLines = selfCloneLines;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
