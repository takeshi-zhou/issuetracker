package cn.edu.fudan.issueservice.domain;

import java.io.Serializable;

/**
 * @author WZY
 * @version 1.0
 **/
public class IssueCount implements Serializable {

    private int newIssueCount;

    private int eliminatedIssueCount;

    private int remainingIssueCount;

    public IssueCount(){}

    public IssueCount(int newIssueCount, int eliminatedIssueCount, int remainingIssueCount) {
        this.newIssueCount = newIssueCount;
        this.eliminatedIssueCount = eliminatedIssueCount;
        this.remainingIssueCount = remainingIssueCount;
    }

    public void issueCountUpdate(int newIssueCount, int eliminatedIssueCount, int remainingIssueCount) {
        this.newIssueCount += newIssueCount;
        this.eliminatedIssueCount += eliminatedIssueCount;
        this.remainingIssueCount += remainingIssueCount;
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

    public int getRemainingIssueCount() {
        return remainingIssueCount;
    }

    public void setRemainingIssueCount(int remainingIssueCount) {
        this.remainingIssueCount = remainingIssueCount;
    }

    @Override
    public String toString() {
        return "IssueCount{" +
                "newIssueCount=" + newIssueCount +
                ", eliminatedIssueCount=" + eliminatedIssueCount +
                ", remainingIssueCount=" + remainingIssueCount +
                '}';
    }
}
