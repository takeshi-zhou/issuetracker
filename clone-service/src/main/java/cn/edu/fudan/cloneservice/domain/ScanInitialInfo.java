package cn.edu.fudan.cloneservice.domain;

import java.util.List;

/**
 * @author WZY
 * @version 1.0
 **/
public class ScanInitialInfo {

    private String repoId;
    private List<String> commitList;

    public ScanInitialInfo(String repoId, List<String> commitList) {
        this.repoId = repoId;
        this.commitList = commitList;
    }

    public String getRepoId() {
        return repoId;
    }

    public void setRepoId(String repoId) {
        this.repoId = repoId;
    }

    public List<String> getCommitList() {
        return commitList;
    }

    public void setCommitList(List<String> commitList) {
        this.commitList = commitList;
    }
}
