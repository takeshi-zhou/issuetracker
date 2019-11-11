package cn.edu.fudan.measureservice.domain;


public class CommitBaseInfoMonthly {

    private String month;
    private CommitBase commitBase;


    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public CommitBase getCommitBaseInfo() {
        return commitBase;
    }

    public void setCommitBaseInfo(CommitBase commitBase) {
        this.commitBase = commitBase;
    }

}