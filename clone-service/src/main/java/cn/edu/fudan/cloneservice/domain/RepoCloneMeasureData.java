package cn.edu.fudan.cloneservice.domain;

/**
 * Created by njzhan
 * <p>
 * Date :2019-08-19
 * <p>
 * Description :
 * <p>
 * Version :1.0
 */
public class RepoCloneMeasureData {
    String repo_id;
    String commit_id;
    long clone_line;
    long total_line;

    public RepoCloneMeasureData(String repo_id, String commit_id, long clone_line, long total_line) {
        this.repo_id = repo_id;
        this.commit_id = commit_id;
        this.clone_line = clone_line;
        this.total_line = total_line;
    }

    public String getRepo_id() {
        return repo_id;
    }

    public void setRepo_id(String repo_id) {
        this.repo_id = repo_id;
    }

    public String getCommit_id() {
        return commit_id;
    }

    public void setCommit_id(String commit_id) {
        this.commit_id = commit_id;
    }

    public long getClone_line() {
        return clone_line;
    }

    public void setClone_line(long clone_line) {
        this.clone_line = clone_line;
    }

    public long getTotal_line() {
        return total_line;
    }

    public void setTotal_line(long total_line) {
        this.total_line = total_line;
    }
}
