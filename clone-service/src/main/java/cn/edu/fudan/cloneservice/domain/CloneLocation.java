package cn.edu.fudan.cloneservice.domain;

/**
 * @author zyh
 * @date 2020/4/28
 * uuid,type,bug_lines,file_path,rawIssue_id,repo_id,commit_id,category
 */
public class CloneLocation {

    private String uuid;
    private String type;
    private String bugLines;
    private String filePath;
    private String rawIssueId;
    private String repoId;
    private String commitId;
    private String category;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBugLines() {
        return bugLines;
    }

    public void setBugLines(String bugLines) {
        this.bugLines = bugLines;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getRawIssueId() {
        return rawIssueId;
    }

    public void setRawIssueId(String rawIssueId) {
        this.rawIssueId = rawIssueId;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
