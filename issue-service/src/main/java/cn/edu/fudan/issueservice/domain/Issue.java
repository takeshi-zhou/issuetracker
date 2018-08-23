package cn.edu.fudan.issueservice.domain;

import java.util.Date;
import java.util.List;

public class Issue {

    private String uuid;
    private String type;
    private String start_commit;
    private String end_commit;
    private String raw_issue_start;
    private String raw_issue_end;
    private String project_id;
    private String target_files;
    private List<RawIssue> rawIssues;
    private IssueType issueType;

    public IssueType getIssueType() {
        return issueType;
    }

    public void setIssueType(IssueType issueType) {
        this.issueType = issueType;
    }

    public Issue() {
    }

    public Issue(String uuid, String type, String start_commit,String end_commit,String raw_issue_start, String raw_issue_end,String project_id,String target_files) {
        this.uuid = uuid;
        this.type = type;
        this.start_commit=start_commit;
        this.end_commit=end_commit;
        this.raw_issue_start = raw_issue_start;
        this.raw_issue_end = raw_issue_end;
        this.project_id=project_id;
        this.target_files=target_files;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStart_commit() {
        return start_commit;
    }

    public void setStart_commit(String start_commit) {
        this.start_commit = start_commit;
    }

    public String getEnd_commit() {
        return end_commit;
    }

    public void setEnd_commit(String end_commit) {
        this.end_commit = end_commit;
    }

    public String getRaw_issue_start() {
        return raw_issue_start;
    }

    public void setRaw_issue_start(String raw_issue_start) {
        this.raw_issue_start = raw_issue_start;
    }

    public String getRaw_issue_end() {
        return raw_issue_end;
    }

    public void setRaw_issue_end(String raw_issue_end) {
        this.raw_issue_end = raw_issue_end;
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

    public List<RawIssue> getRawIssues() {
        return rawIssues;
    }

    public void setRawIssues(List<RawIssue> rawIssues) {
        this.rawIssues = rawIssues;
    }

    public String getTarget_files() {
        return target_files;
    }

    public void setTarget_files(String target_files) {
        this.target_files = target_files;
    }
}
