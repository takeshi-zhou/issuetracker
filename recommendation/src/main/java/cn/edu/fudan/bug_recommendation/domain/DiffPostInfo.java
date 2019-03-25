package cn.edu.fudan.bug_recommendation.domain;

public class DiffPostInfo {
    private String commitid;
    private String nextcommitid;
    private String location;

    public DiffPostInfo(){
    }

    public DiffPostInfo(String commitid,String nextcommitid,String location){
        this.commitid = commitid;
        this.nextcommitid = nextcommitid;
        this.location = location;
    }

    public String getCommitid() {
        return commitid;
    }

    public void setCommitid(String commitid) {
        this.commitid = commitid;
    }

    public String getNextcommitid() {
        return nextcommitid;
    }

    public void setNextcommitid(String nextcommitid) {
        this.nextcommitid = nextcommitid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
