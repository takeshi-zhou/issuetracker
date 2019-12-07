package cn.edu.fudan.issueservice.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @author WZY
 * @version 1.0
 **/
public class IssueCount implements Serializable {

    private String date;

    private int newIssueCount;

    private int eliminatedIssueCount;

    private int remainingIssueCount;

    private List<String> newIssueIds;

    private List<String> eliminatedIssueIds;

    public IssueCount() {
    }

    public IssueCount(int newIssueCount, int eliminatedIssueCount, int remainingIssueCount) {
        this(newIssueCount,eliminatedIssueCount,remainingIssueCount,null,null);
    }

    public IssueCount(String date,int newIssueCount, int eliminatedIssueCount, int remainingIssueCount) {
        this.date=date;
        this.newIssueCount = newIssueCount;
        this.eliminatedIssueCount = eliminatedIssueCount;
        this.remainingIssueCount = remainingIssueCount;
    }

    public IssueCount(int newIssueCount, int eliminatedIssueCount, int remainingIssueCount,List<String> newIssueIds,List<String> eliminatedIssueIds) {
        this.newIssueCount = newIssueCount;
        this.eliminatedIssueCount = eliminatedIssueCount;
        this.remainingIssueCount = remainingIssueCount;
        this.newIssueIds=newIssueIds;
        this.eliminatedIssueIds=eliminatedIssueIds;
    }

    public void issueCountUpdate(int newIssueCount, int eliminatedIssueCount, int remainingIssueCount) {
        this.newIssueCount += newIssueCount;
        this.eliminatedIssueCount += eliminatedIssueCount;
        this.remainingIssueCount = remainingIssueCount;
    }

    public void issueCountUpdate(IssueCount another) {
        this.newIssueCount += another.getNewIssueCount();
        this.eliminatedIssueCount += another.getEliminatedIssueCount();
        this.remainingIssueCount += another.getRemainingIssueCount();
        if(this.newIssueIds==null) {
            this.newIssueIds = another.newIssueIds;
        } else{
            this.newIssueIds.addAll(another.newIssueIds);
        }
        if(this.eliminatedIssueIds==null) {
            this.eliminatedIssueIds=another.eliminatedIssueIds;
        } else{
            this.eliminatedIssueIds.addAll(another.eliminatedIssueIds);
        }
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public List<String> getNewIssueIds() {
        return newIssueIds;
    }

    public void setNewIssueIds(List<String> newIssueIds) {
        this.newIssueIds = newIssueIds;
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
