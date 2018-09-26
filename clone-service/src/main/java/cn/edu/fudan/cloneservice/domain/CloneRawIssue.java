package cn.edu.fudan.cloneservice.domain;

import java.util.List;

/**
 * @author WZY
 * @version 1.0
 **/
public class CloneRawIssue {

    private String uuid;
    private String group_id;
    private String scan_id;
    private String clone_issue_id;
    private String commit_id;
    private List<CloneLocation> locations;

    public List<CloneLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<CloneLocation> locations) {
        this.locations = locations;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getScan_id() {
        return scan_id;
    }

    public void setScan_id(String scan_id) {
        this.scan_id = scan_id;
    }

    public String getClone_issue_id() {
        return clone_issue_id;
    }

    public void setClone_issue_id(String clone_issue_id) {
        this.clone_issue_id = clone_issue_id;
    }

    public String getCommit_id() {
        return commit_id;
    }

    public void setCommit_id(String commit_id) {
        this.commit_id = commit_id;
    }
}
