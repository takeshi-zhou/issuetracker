package cn.edu.fudan.measureservice.domain;

import java.util.List;
import java.util.Map;

public class RepoAllInformations {
    private String repoName;
    private String branch;
    private String repoUrl;
    private List<Developer> developers;
    private int files;
    private int ncss;
    private int classes;
    private int commitTimes;
    private int commitAdds;
    private int commitDels;
    private double ccn;
    private Map<String,Integer> IssuesCounts;
    private int issueEliminatedCount;
    private int issueRemainedCount;
    private int issueIncrements;

    public RepoAllInformations(){
        super();
    }

    public RepoAllInformations(RepoMeasure repoMeasure){
        this.files=repoMeasure.getFiles();
        this.ncss = repoMeasure.getNcss();
        this.classes = repoMeasure.getClasses();
        this.ccn = repoMeasure.getCcn();
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public String getRepoName() {
        return repoName;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public List<Developer> getDevelopers() {
        return developers;
    }

    public void setDevelopers(List<Developer> developers) {
        this.developers = developers;
    }

    public int getFiles() {
        return files;
    }

    public void setFiles(int files) {
        this.files = files;
    }

    public int getNcss() {
        return ncss;
    }

    public void setNcss(int ncss) {
        this.ncss = ncss;
    }

    public int getClasses() {
        return classes;
    }

    public void setClasses(int classes) {
        this.classes = classes;
    }

    public int getCommitTimes() {
        return commitTimes;
    }

    public void setCommitTimes(int commitTimes) {
        this.commitTimes = commitTimes;
    }

    public int getCommitAdds() {
        return commitAdds;
    }

    public void setCommitAdds(int commitAdds) {
        this.commitAdds = commitAdds;
    }

    public int getCommitDels() {
        return commitDels;
    }

    public void setCommitDels(int commitDels) {
        this.commitDels = commitDels;
    }

    public double getCcn() {
        return ccn;
    }

    public void setCcn(double ccn) {
        this.ccn = ccn;
    }

    public Map<String, Integer> getIssuesCounts() {
        return IssuesCounts;
    }

    public void setIssuesCounts(Map<String, Integer> issuesCounts) {
        IssuesCounts = issuesCounts;
    }

    public int getIssueEliminatedCount() {
        return issueEliminatedCount;
    }

    public void setIssueEliminatedCount(int issueEliminatedCount) {
        this.issueEliminatedCount = issueEliminatedCount;
    }

    public int getIssueRemainedCount() {
        return issueRemainedCount;
    }

    public void setIssueRemainedCount(int issueRemainedCount) {
        this.issueRemainedCount = issueRemainedCount;
    }

    public int getIssueIncrements() {
        return issueIncrements;
    }

    public void setIssueIncrements(int issueIncrements) {
        this.issueIncrements = issueIncrements;
    }
}
