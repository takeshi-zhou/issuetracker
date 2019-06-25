package cn.edu.fudan.issueservice.domain;

public class IssueCountDeveloper {

    private String developer;

    private int newIssueCount;

    private int eliminatedIssueCount;

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

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
