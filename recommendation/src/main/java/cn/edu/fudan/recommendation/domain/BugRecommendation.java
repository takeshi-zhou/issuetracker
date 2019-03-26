package cn.edu.fudan.recommendation.domain;

import java.io.Serializable;

public class BugRecommendation implements Serializable {
    private String uuid;
    private String type;
    private String location;
    private String description;
    private String bugLines;
    private Integer start_line;
    private Integer end_line;
    private String code;
    private Integer nextstart_line;
    private Integer nextend_line;
    private String prev_code;
    private String curr_code;
    private String curr_commitid;
    private String next_commitid;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getNextstart_line() {
        return nextstart_line;
    }

    public void setNextstart_line(Integer nextstart_line) {
        this.nextstart_line = nextstart_line;
    }

    public Integer getNextend_line() {
        return nextend_line;
    }

    public void setNextend_line(Integer nextend_line) {
        this.nextend_line = nextend_line;
    }



    public Integer getStart_line() {
        return start_line;
    }

    public void setStart_line(Integer start_line) {
        this.start_line = start_line;
    }

    public Integer getEnd_line() {
        return end_line;
    }

    public void setEnd_line(Integer end_line) {
        this.end_line = end_line;
    }

    public String getCurr_commitid() {
        return curr_commitid;
    }

    public void setCurr_commitid(String curr_commitid) {
        this.curr_commitid = curr_commitid;
    }

    public String getNext_commitid() {
        return next_commitid;
    }

    public void setNext_commitid(String next_commitid) {
        this.next_commitid = next_commitid;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBugLines() {
        return bugLines;
    }

    public void setBugLines(String bugLines) {
        this.bugLines = bugLines;
    }



    public String getPrev_code() {
        return prev_code;
    }

    public void setPrev_code(String prev_code) {
        this.prev_code = prev_code;
    }

    public String getCurr_code() {
        return curr_code;
    }

    public void setCurr_code(String curr_code) {
        this.curr_code = curr_code;
    }



}
