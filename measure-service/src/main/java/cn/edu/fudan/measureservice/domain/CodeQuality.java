package cn.edu.fudan.measureservice.domain;

public class CodeQuality {
    private String expression;
    private String date;
    private double result;
    private String commitId;
    private String projectName;

    public CodeQuality(){
        super();
    }

    public CodeQuality(int addLines,int delLines, int newIssues,String date){

        this.expression = newIssues + "/(" + addLines + "+" + delLines + ")";
        this.date = date;
        if(addLines != 0 && delLines != 0 && newIssues !=-1){
            this.result = newIssues*1.0/(addLines+delLines);
        }else{
            result=0;
        }


    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(int addLines,int delLines, int newIssues) {
        this.expression = newIssues + "/(" + addLines + "+" + delLines + ")";
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
