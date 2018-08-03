package cn.edu.fudan.scanscheduler.domain;

/**
 * @author WZY
 * @version 1.0
 **/
public class ScanInitialInfo {

    private Scan scan;
    private String projectName;
    private String repoId;
    private String repoPath;

    public ScanInitialInfo(Scan scan, String projectName, String repoId, String repoPath) {
        this.scan = scan;
        this.projectName = projectName;
        this.repoId = repoId;
        this.repoPath = repoPath;
    }

    public Scan getScan() {
        return scan;
    }

    public void setScan(Scan scan) {
        this.scan = scan;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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
}
