package cn.edu.fudan.bug_recommendation.domain;


public class BugSolvedBasicInfo {
    private String type;
    private String location;
    private String bug_lines;
    private Integer start_line;
    private Integer end_line;
    private String curr_commitid;
    private String next_commitid;
    private String repoid;

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

    public String getBug_lines() {
        return bug_lines;
    }

    public void setBug_lines(String bug_lines) {
        this.bug_lines = bug_lines;
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

    public String getRepoid() {
        return repoid;
    }

    public void setRepoid(String repoid) {
        this.repoid = repoid;
    }



}
