package cn.edu.fudan.cloneservice.domain;

/**
 * @author WZY
 * @version 1.0
 **/
public class CloneLocation {
    private String uuid;
    private int start_line;
    private int end_line;
    private int start_token;
    private int end_token;
    private String file_path;
    private String group_id;
    private String clone_rawIssue_id;
    private String code;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getStart_line() {
        return start_line;
    }

    public void setStart_line(int start_line) {
        this.start_line = start_line;
    }

    public int getEnd_line() {
        return end_line;
    }

    public void setEnd_line(int end_line) {
        this.end_line = end_line;
    }

    public int getStart_token() {
        return start_token;
    }

    public void setStart_token(int start_token) {
        this.start_token = start_token;
    }

    public int getEnd_token() {
        return end_token;
    }

    public void setEnd_token(int end_token) {
        this.end_token = end_token;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getClone_rawIssue_id() {
        return clone_rawIssue_id;
    }

    public void setClone_rawIssue_id(String clone_rawIssue_id) {
        this.clone_rawIssue_id = clone_rawIssue_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
