package cn.edu.fudan.cloneservice.domain;

import java.util.List;

/**
 * @author WZY
 * @version 1.0
 **/
public class ScanInitialInfo {

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getRepoId() {
        return repoId;
    }

    public void setRepoId(String repoId) {
        this.repoId = repoId;
    }

    public String getRepoPath() {
        return repoPath;
    }

    public void setRepoPath(String repoPath) {
        this.repoPath = repoPath;
    }

//    private PackageScanStatus packageScanStatus;
    private String repoName;
    private String repoId;
    private String repoPath;
    private List<String> commitList;

    public List<String> getCommitList() {
        return commitList;
    }

    public void setCommitList(List<String> commitList) {
        this.commitList = commitList;
    }

    public ScanInitialInfo(String repoName, String repoId, String repoPath) {

//        this.scan = scan;
        this.repoName = repoName;
        this.repoId = repoId;
        this.repoPath = repoPath;
    }


}
