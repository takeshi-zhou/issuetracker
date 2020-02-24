package cn.edu.fudan.measureservice.domain;



import java.util.ArrayList;
import java.util.List;

public class CommitBaseInfoDuration {

    //查询一段时间内的commitBaseInfo 返回以下数据
    private List<CommitInfoDeveloper> commitInfoList;
    private int sumAddLines;
    private int sumDelLines;
    private int sumCommitCounts;
    private int sumChangedFiles;



    public int getSumCommitCounts() {
        return sumCommitCounts;
    }

    public void setSumCommitCounts(int sumCommitCounts) {
        this.sumCommitCounts = sumCommitCounts;
    }

    public CommitBaseInfoDuration() {
    }

    public List<CommitInfoDeveloper> getCommitInfoList() {
        return commitInfoList;
    }

    public void setCommitInfoList(List<CommitInfoDeveloper> commitInfoList) {
        this.commitInfoList = commitInfoList;
    }

    public int getSumAddLines() {
        return sumAddLines;
    }

    public void setSumAddLines(int sumAddLines) {
        this.sumAddLines = sumAddLines;
    }

    public int getSumDelLines() {
        return sumDelLines;
    }

    public void setSumDelLines(int sumDelLines) {
        this.sumDelLines = sumDelLines;
    }

    public int getSumChangedFiles() {
        return sumChangedFiles;
    }

    public void setSumChangedFiles(int sumChangedFiles) {
        this.sumChangedFiles = sumChangedFiles;
    }
}