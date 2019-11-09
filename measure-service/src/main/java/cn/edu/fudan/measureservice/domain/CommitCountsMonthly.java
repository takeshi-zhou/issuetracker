package cn.edu.fudan.measureservice.domain;


public class CommitCountsMonthly {

    private String month;
    private int commit_counts;


    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getCommit_counts() {
        return commit_counts;
    }

    public void setCommit_counts(int commit_counts) {
        this.commit_counts = commit_counts;
    }

}