package cn.edu.fudan.bug_recommendation.domain;

public class DiffBugInfo {
    private String buglines;
    private String bugCode;
    private String bugtype;
    private int endline;
    private int startline;

    public DiffBugInfo(){
    }


    public DiffBugInfo(String buglines,String bugCode,int endline,int startline){
        this.buglines = buglines;
        this.bugCode = bugCode;
        this.endline = endline;
        this.startline = startline;
    }
    public String getBugtype() {
        return bugtype;
    }

    public void setBugtype(String bugtype) {
        this.bugtype = bugtype;
    }

    public String getBuglines() {
        return buglines;
    }

    public void setBuglines(String buglines) {
        this.buglines = buglines;
    }

    public String getBugCode() {
        return bugCode;
    }

    public void setBugCode(String bugCode) {
        this.bugCode = bugCode;
    }

    public int getEndline() {
        return endline;
    }

    public void setEndline(int endline) {
        this.endline = endline;
    }

    public int getStartline() {
        return startline;
    }

    public void setStartline(int startline) {
        this.startline = startline;
    }


}
