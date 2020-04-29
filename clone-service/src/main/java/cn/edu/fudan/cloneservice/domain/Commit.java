package cn.edu.fudan.cloneservice.domain;

public class Commit {
    String uuid;
    String repo_id;
    String commit_id;
    String commit_time;
    String developer;

    public Commit(String uuid, String repo_id, String commit_id, String commit_time, String developer) {
        this.uuid = uuid;
        this.repo_id = repo_id;
        this.commit_id = commit_id;
        this.commit_time = commit_time;
        this.developer = developer;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getCommit_time() {
        return commit_time;
    }

    public void setCommit_time(String commit_time) {
        this.commit_time = commit_time;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
}
