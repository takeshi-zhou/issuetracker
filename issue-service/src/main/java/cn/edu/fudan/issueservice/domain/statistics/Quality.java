package cn.edu.fudan.issueservice.domain.statistics;

public class Quality {

    private int newIssues;
    private int eliminateIssues;
    private double eliminateIssueQuality;
    private double addIssueQuality;

    public int getNewIssues() {
        return newIssues;
    }

    public void setNewIssues(int newIssues) {
        this.newIssues = newIssues;
    }

    public int getEliminateIssues() {
        return eliminateIssues;
    }

    public void setEliminateIssues(int eliminateIssues) {
        this.eliminateIssues = eliminateIssues;
    }

    public double getEliminateIssueQuality() {
        return eliminateIssueQuality;
    }

    public void setEliminateIssueQuality(double eliminateIssueQuality) {
        this.eliminateIssueQuality = eliminateIssueQuality;
    }


    public double getAddIssueQuality() {
        return addIssueQuality;
    }

    public void setAddIssueQuality(double addIssueQuality) {
        this.addIssueQuality = addIssueQuality;
    }

    public void setEliminateIssueQualityThroughCalculate(double eliminateIssues,double totalChangedLines) {
        if(totalChangedLines ==0){
            this.eliminateIssueQuality = -1;
        }
        this.eliminateIssueQuality = eliminateIssues*100.0/totalChangedLines;
    }

    public void setAddIssueQualityThroughCalculate(double newIssues,double totalChangedLines) {
        if(totalChangedLines ==0){
            this.addIssueQuality = -1;
        }
        this.addIssueQuality = newIssues*100.0/totalChangedLines;
    }
}
