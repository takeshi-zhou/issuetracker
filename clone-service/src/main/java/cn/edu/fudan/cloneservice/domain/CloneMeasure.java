package cn.edu.fudan.cloneservice.domain;

/**
 * @author zyh
 * @date 2020/4/28
 */
public class CloneMeasure {

    private String uuid;
    private String repoId;
    private String commitId;

    private String increasedCloneLines;
    private String selfIncreasedCloneLines;
    private String increasedCloneLinesRate;

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

    public String getIncreasedCloneLines() {
        return increasedCloneLines;
    }

    public void setIncreasedCloneLines(String increasedCloneLines) {
        this.increasedCloneLines = increasedCloneLines;
    }

    public String getSelfIncreasedCloneLines() {
        return selfIncreasedCloneLines;
    }

    public void setSelfIncreasedCloneLines(String selfIncreasedCloneLines) {
        this.selfIncreasedCloneLines = selfIncreasedCloneLines;
    }

    public String getIncreasedCloneLinesRate() {
        return increasedCloneLinesRate;
    }

    public void setIncreasedCloneLinesRate(String increasedCloneLinesRate) {
        this.increasedCloneLinesRate = increasedCloneLinesRate;
    }
}
