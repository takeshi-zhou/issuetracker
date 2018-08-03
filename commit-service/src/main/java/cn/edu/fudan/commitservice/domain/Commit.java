package cn.edu.fudan.commitservice.domain;

import java.util.Date;

public class Commit {

    private String uuid;
    private String commit_id;
    private String message;
    private String developer;
    private Date commit_time;
    private int is_scanned;
    private String project_id;
    private String repo_id;

    public Commit(){

    }

    public Commit(String commit_id, int is_scanned){
        this.commit_id=commit_id;
        this.is_scanned=is_scanned;
    }

    public Commit(String uuid, String commit_id, String message, String developer, Date commit_time, int is_scanned, String project_id, String repo_id) {
        this.uuid = uuid;
        this.commit_id = commit_id;
        this.message = message;
        this.developer = developer;
        this.commit_time = commit_time;
        this.is_scanned=is_scanned;
        this.project_id=project_id;
        this.repo_id=repo_id;
    }

    public int getIs_scanned() {
        return is_scanned;
    }

    public void setIs_scanned(int is_scanned) {
        this.is_scanned = is_scanned;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public Date getCommit_time() {
        return commit_time;
    }

    public void setCommit_time(Date commit_time) {
        this.commit_time = commit_time;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }
}
