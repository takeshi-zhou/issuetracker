package cn.edu.fudan.measureservice.domain.test;

public class Commit{
    private String uuid;
    private String commit_id;
    private String message;
    private String developer;
    private String commit_time;
    private String repo_id;
    private String developer_email;
    private String self_index;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getCommit_time() {
        return commit_time;
    }

    public void setCommit_time(String commit_time) {
        this.commit_time = commit_time;
    }

    public String getRepo_id() {
        return repo_id;
    }

    public void setRepo_id(String repo_id) {
        this.repo_id = repo_id;
    }

    public String getDeveloper_email() {
        return developer_email;
    }

    public void setDeveloper_email(String developer_email) {
        this.developer_email = developer_email;
    }

    public String getSelf_index() {
        return self_index;
    }

    public void setSelf_index(String self_index) {
        this.self_index = self_index;
    }
}
