package cn.edu.fudan.scanscheduler.domain;

public class ScanResult {
    private String projectId;
    private String commitId;
    private String status;
    private String description;

    public ScanResult() {
    }

    public ScanResult(String status, String description) {
        this.status = status;
        this.description = description;
    }

    public ScanResult(String projectId, String commitId, String status, String description) {
        this.projectId = projectId;
        this.commitId = commitId;
        this.status = status;
        this.description = description;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
