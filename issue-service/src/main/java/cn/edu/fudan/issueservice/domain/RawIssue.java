package cn.edu.fudan.issueservice.domain;

import java.util.Date;
import java.util.List;

public class RawIssue {

    private String uuid;
    private String type;
    private String category;
    private String detail;
    private String file_name;
    private String scan_id;
    private String issue_id;
    private String commit_id;
    private String repo_id;
    private int code_lines;
    private String status;
    private List<Location> locations;
    private Date commit_time;
    private String developer_email;
    private Issue issue;

    private boolean mapped=false;

    public boolean isMapped() {
        return mapped;
    }

    public void setMapped(boolean mapped) {
        this.mapped = mapped;
    }

    public String getDeveloper_email() {
        return developer_email;
    }

    public void setDeveloper_email(String developer_email) {
        this.developer_email = developer_email;
    }

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }


    public String getScan_id() {
        return scan_id;
    }

    public void setScan_id(String scan_id) {
        this.scan_id = scan_id;
    }

    public String getIssue_id() {
        return issue_id;
    }

    public void setIssue_id(String issue_id) {
        this.issue_id = issue_id;
    }

    public String getCommit_id() {
        return commit_id;
    }

    public void setCommit_id(String commit_id) {
        this.commit_id = commit_id;
    }

    public String getRepo_id() {
        return repo_id;
    }

    public void setRepo_id(String repo_id) {
        this.repo_id = repo_id;
    }

    public int getCode_lines() {
        return code_lines;
    }

    public void setCode_lines(int code_lines) {
        this.code_lines = code_lines;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Location firstLocation() {
        return this.locations.get(0);
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public Date getCommit_time() {
        if(commit_time == null){
            return null;
        }
        return (Date) commit_time.clone();
    }

    public void setCommit_time(Date commit_time) {
        if(commit_time == null){
            this.commit_time = null;
        }else {
            this.commit_time = (Date) commit_time.clone();
        }
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + uuid.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + detail.hashCode();
        result = 31 * result + scan_id.hashCode();
        result = 31 * result + commit_id.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RawIssue)) {
            return false;
        }
        RawIssue rawIssue = (RawIssue) obj;

        return rawIssue.getUuid().equals(((RawIssue) obj).getUuid());

/*        if (this.detail != null && ((RawIssue) obj).detail != null) {
            return rawIssue.detail.equals(detail) &&
                    rawIssue.file_name.equals(file_name);
        }

        if(this.detail == null || ((RawIssue) obj).detail == null) {
            return rawIssue.type.equals(type) &&
                    rawIssue.file_name.equals(file_name);
        } else {
            return rawIssue.type.equals(type) &&
                    rawIssue.detail.equals(detail) &&
                    rawIssue.file_name.equals(file_name);

        }
        return false;*/
    }

    public boolean hasSameBaseInfo(RawIssue obj) {
        if (this.detail != null && obj.detail != null) {
            return obj.detail.equals(detail) &&
                    obj.file_name.equals(file_name);
        }
        return false;
    }

    @Override
    public String toString() {
        return "{uuid="+uuid+",type="+type+",category="+category+",detail="+detail+"}";
    }
}