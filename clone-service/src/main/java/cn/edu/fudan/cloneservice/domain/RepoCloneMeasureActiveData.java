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
public class RepoCloneMeasureActiveData {
    String repo_id;
    String start_commit_id;
    String start_commit_date;
    String end_commit_id;
    String end_commit_date;
    Integer clone_line;

    public RepoCloneMeasureActiveData(String repo_id, String start_commit_id, String start_commit_date, String end_commit_id, String end_commit_date, Integer clone_line) {
        this.repo_id = repo_id;
        this.start_commit_id = start_commit_id;
        this.start_commit_date = start_commit_date;
        this.end_commit_id = end_commit_id;
        this.end_commit_date = end_commit_date;
        this.clone_line = clone_line;
    }

    public String getRepo_id() {
        return repo_id;
    }

    public void setRepo_id(String repo_id) {
        this.repo_id = repo_id;
    }

    public String getStart_commit_id() {
        return start_commit_id;
    }

    public void setStart_commit_id(String start_commit_id) {
        this.start_commit_id = start_commit_id;
    }

    public String getStart_commit_date() {
        return start_commit_date;
    }

    public void setStart_commit_date(String start_commit_date) {
        this.start_commit_date = start_commit_date;
    }

    public String getEnd_commit_id() {
        return end_commit_id;
    }

    public void setEnd_commit_id(String end_commit_id) {
        this.end_commit_id = end_commit_id;
    }

    public String getEnd_commit_date() {
        return end_commit_date;
    }

    public void setEnd_commit_date(String end_commit_date) {
        this.end_commit_date = end_commit_date;
    }

    public Integer getClone_line() {
        return clone_line;
    }

    public void setClone_line(Integer clone_line) {
        this.clone_line = clone_line;
    }
}
