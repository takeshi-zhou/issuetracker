package cn.edu.fudan.issueservice.domain;

public class IssueCountMeasure {

    private int newIssueCount;

    private int eliminatedIssueCount;


    public int getNewIssueCount() {
        return newIssueCount;
    }

    public void setNewIssueCount(int newIssueCount) {
        this.newIssueCount = newIssueCount;
    }

    public int getEliminatedIssueCount() {
        return eliminatedIssueCount;
    }

    public void setEliminatedIssueCount(int eliminatedIssueCount) {
        this.eliminatedIssueCount = eliminatedIssueCount;
    }
}
