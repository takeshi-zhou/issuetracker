package cn.edu.fudan.measureservice.domain;

public class ObjectAverage {
    private double ncss;
    private double functions;
    private double classes;
    private double javaDocs;
    private double javaDocsLines;
    private double singleCommentLines;
    private double implementationCommentLines;

    public double getNcss() {
        return ncss;
    }

    public void setNcss(double ncss) {
        this.ncss = ncss;
    }

    public double getFunctions() {
        return functions;
    }

    public void setFunctions(double functions) {
        this.functions = functions;
    }

    public double getClasses() {
        return classes;
    }

    public void setClasses(double classes) {
        this.classes = classes;
    }

    public double getJavaDocs() {
        return javaDocs;
    }

    public void setJavaDocs(double javaDocs) {
        this.javaDocs = javaDocs;
    }

    public double getJavaDocsLines() {
        return javaDocsLines;
    }

    public void setJavaDocsLines(double javaDocsLines) {
        this.javaDocsLines = javaDocsLines;
    }

    public double getSingleCommentLines() {
        return singleCommentLines;
    }

    public void setSingleCommentLines(double singleCommentLines) {
        this.singleCommentLines = singleCommentLines;
    }

    public double getImplementationCommentLines() {
        return implementationCommentLines;
    }

    public void setImplementationCommentLines(double implementationCommentLines) {
        this.implementationCommentLines = implementationCommentLines;
    }
}
