package cn.edu.fudan.measureservice.domain;

import java.util.List;

public class ActiveMeasure {

    private List<CommitInfo> commitInfos;
    private List<String> mostCommitFiles;

    public List<CommitInfo> getCommitInfos() {
        return commitInfos;
    }

    public void setCommitInfos(List<CommitInfo> commitInfos) {
        this.commitInfos = commitInfos;
    }

    public List<String> getMostCommitFiles() {
        return mostCommitFiles;
    }

    public void setMostCommitFiles(List<String> mostCommitFiles) {
        this.mostCommitFiles = mostCommitFiles;
    }
}
