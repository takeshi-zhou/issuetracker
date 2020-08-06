package cn.edu.fudan.cloneservice.domain;

public class ScanResult {
    private String repoId;
    private String commitId;
    private String type;
    private String status;
    private String description;

    public ScanResult() {
    }

    public ScanResult(String type, String status, String description) {
        this.type=type;
        this.status = status;
        this.description = description;
    }

    public ScanResult(String repoId, String commitId, String type, String status, String description) {
        this.repoId = repoId;
        this.commitId = commitId;
        this.type=type;
        this.status = status;
        this.description = description;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
