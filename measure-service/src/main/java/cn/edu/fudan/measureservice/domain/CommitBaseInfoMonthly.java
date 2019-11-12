package cn.edu.fudan.measureservice.domain;


public class CommitBaseInfoMonthly {

    private String month;
    private CommitBaseInfoDuration commitBaseInfoDuration;


    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public CommitBaseInfoDuration getCommitBaseInfo() {
        return commitBaseInfoDuration;
    }

    public void setCommitBaseInfo(CommitBaseInfoDuration commitBaseInfoDuration) {
        this.commitBaseInfoDuration = commitBaseInfoDuration;
    }

}