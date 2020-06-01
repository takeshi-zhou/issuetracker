package cn.edu.fudan.cloneservice.domain;

/**
 * @author zyh
 * @date 2020/4/28
 */
public class CloneMeasure {

    private String uuid;
    private String repoId;
    private String commitId;

    private int newCloneLines;
    private int selfCloneLines;
    private int addLines;
    private int cloneLines;
    private int preCloneLines;

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

    public int getNewCloneLines() {
        return newCloneLines;
    }

    public void setNewCloneLines(int newCloneLines) {
        this.newCloneLines = newCloneLines;
    }

    public int getSelfCloneLines() {
        return selfCloneLines;
    }

    public void setSelfCloneLines(int selfCloneLines) {
        this.selfCloneLines = selfCloneLines;
    }

    public int getAddLines() {
        return addLines;
    }

    public void setAddLines(int addLines) {
        this.addLines = addLines;
    }

    public int getCloneLines() {
        return cloneLines;
    }

    public void setCloneLines(int cloneLines) {
        this.cloneLines = cloneLines;
    }

    public int getPreCloneLines() {
        return preCloneLines;
    }

    public void setPreCloneLines(int preCloneLines) {
        this.preCloneLines = preCloneLines;
    }
}
