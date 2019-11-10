package cn.edu.fudan.measureservice.domain;

public class CodeQuality {
    private String expression;
    private String date;
    private double result;

    public CodeQuality(){
        super();
    }

    public CodeQuality(int addLines,int delLines, int newIssues,String date){

        this.expression = newIssues + "/(" + addLines + "+" + delLines + ")";
        this.date = date;
        this.result = newIssues/(addLines+delLines);

    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(int addLines,int delLines, int newIssues) {
        this.expression = newIssues + "/(" + addLines + "+" + delLines + ")";
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
}
