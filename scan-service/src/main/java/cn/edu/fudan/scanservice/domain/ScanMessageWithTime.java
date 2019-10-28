package cn.edu.fudan.scanservice.domain;


/**
 * @author WZY
 * @version 1.0
 **/
public class ScanMessageWithTime {

    private String repoId;
    private String commitId;
    private String commitTime;
    private String developerName;
    private String developerEmail;

    public ScanMessageWithTime() {
    }

    public ScanMessageWithTime(String repoId, String commitId) {
        this.repoId = repoId;
        this.commitId = commitId;
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

    public String getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(String commitTime) {
        this.commitTime = commitTime;
    }

    public String getDeveloperName() {
        return developerName;
    }

    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
    }

    public String getDeveloperEmail() {
        return developerEmail;
    }

    public void setDeveloperEmail(String developerEmail) {
        this.developerEmail = developerEmail;
    }

    @Override
    public String toString() {
        return "ScanMessageWithTime{" +
                "repoId='" + repoId + '\'' +
                ", commitId='" + commitId + '\'' +
                ", commitTime=" + commitTime + '\'' +
                ", developerName=" + developerName + '\'' +
                ", developerEmail=" + developerEmail +
                '}';
    }
}
