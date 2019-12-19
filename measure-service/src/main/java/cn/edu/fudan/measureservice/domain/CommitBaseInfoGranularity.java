package cn.edu.fudan.measureservice.domain;


public class CommitBaseInfoGranularity {

    private String date;
    private CommitBaseInfoDuration commitBaseInfoDuration;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public CommitBaseInfoDuration getCommitBaseInfoDuration() {
        return commitBaseInfoDuration;
    }

    public void setCommitBaseInfoDuration(CommitBaseInfoDuration commitBaseInfoDuration) {
        this.commitBaseInfoDuration = commitBaseInfoDuration;
    }

}