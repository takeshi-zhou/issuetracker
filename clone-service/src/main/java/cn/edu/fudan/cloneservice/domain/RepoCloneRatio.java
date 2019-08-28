package cn.edu.fudan.cloneservice.domain;

/**
 * Created by njzhan
 * <p>
 * Date :2019-08-28
 * <p>
 * Description :
 * <p>
 * Version :1.0
 */
public class RepoCloneRatio {
    String repo_id;
    String commit_id;
    Double clone_ratio;

    public RepoCloneRatio(String repo_id, String commit_id, Double clone_ratio) {
        this.repo_id = repo_id;
        this.commit_id = commit_id;
        this.clone_ratio = clone_ratio;
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

    public Double getClone_ratio() {
        return clone_ratio;
    }

    public void setClone_ratio(Double clone_ratio) {
        this.clone_ratio = clone_ratio;
    }
}
