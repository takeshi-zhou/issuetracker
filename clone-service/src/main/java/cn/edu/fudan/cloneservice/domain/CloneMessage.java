package cn.edu.fudan.cloneservice.domain;

/**
 * @author zyh
 * @date 2020/5/23
 */
public class CloneMessage {

    private String repoId;
    private String increasedCloneLines;
    private String selfIncreasedCloneLines;
    private String increasedCloneLinesRate;
    private String eliminateCloneLines;
    private String allEliminateCloneLines;

    public CloneMessage(){

    }

    public CloneMessage(String repoId, String increasedCloneLines, String selfIncreasedCloneLines,
                        String increasedCloneLinesRate, String eliminateCloneLines,
                        String allEliminateCloneLines) {
        this.repoId = repoId;
        this.increasedCloneLines = increasedCloneLines;
        this.selfIncreasedCloneLines = selfIncreasedCloneLines;
        this.increasedCloneLinesRate = increasedCloneLinesRate;
        this.eliminateCloneLines = eliminateCloneLines;
        this.allEliminateCloneLines = allEliminateCloneLines;
    }

    public String getRepoId() {
        return repoId;
    }

    public void setRepoId(String repoId) {
        this.repoId = repoId;
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

    public String getEliminateCloneLines() {
        return eliminateCloneLines;
    }

    public void setEliminateCloneLines(String eliminateCloneLines) {
        this.eliminateCloneLines = eliminateCloneLines;
    }

    public String getAllEliminateCloneLines() {
        return allEliminateCloneLines;
    }

    public void setAllEliminateCloneLines(String allEliminateCloneLines) {
        this.allEliminateCloneLines = allEliminateCloneLines;
    }
}
