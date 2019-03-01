package cn.edu.fudan.measureservice.domain;

public class Object {
    private String name;
    private int ncss;
    private int functions;
    private int classes;
    private int javaDocs;
    private int javaDocsLines;
    private int singleCommentLines;
    private int implementationCommentLines;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNcss() {
        return ncss;
    }

    public void setNcss(int ncss) {
        this.ncss = ncss;
    }

    public int getFunctions() {
        return functions;
    }

    public void setFunctions(int functions) {
        this.functions = functions;
    }

    public int getClasses() {
        return classes;
    }

    public void setClasses(int classes) {
        this.classes = classes;
    }

    public int getJavaDocs() {
        return javaDocs;
    }

    public void setJavaDocs(int javaDocs) {
        this.javaDocs = javaDocs;
    }

    public int getJavaDocsLines() {
        return javaDocsLines;
    }

    public void setJavaDocsLines(int javaDocsLines) {
        this.javaDocsLines = javaDocsLines;
    }

    public int getSingleCommentLines() {
        return singleCommentLines;
    }

    public void setSingleCommentLines(int singleCommentLines) {
        this.singleCommentLines = singleCommentLines;
    }

    public int getImplementationCommentLines() {
        return implementationCommentLines;
    }

    public void setImplementationCommentLines(int implementationCommentLines) {
        this.implementationCommentLines = implementationCommentLines;
    }
}
