package cn.edu.fudan.measureservice.domain;

public class Package {

    private String uuid;
    private String name;
    private int classes;
    private int functions;
    private int ncss;
    private int javaDocs;
    private int javaDocsLines;
    private int singleCommentLines;
    private int multiCommentLines;
    private String commit_id;
    private String commit_time;
    private String repo_id;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCommit_id() {
        return commit_id;
    }

    public void setCommit_id(String commit_id) {
        this.commit_id = commit_id;
    }

    public String getCommit_time() {
        return commit_time;
    }

    public void setCommit_time(String commit_time) {
        this.commit_time = commit_time;
    }

    public String getRepo_id() {
        return repo_id;
    }

    public void setRepo_id(String repo_id) {
        this.repo_id = repo_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getClasses() {
        return classes;
    }

    public void setClasses(int classes) {
        this.classes = classes;
    }

    public int getFunctions() {
        return functions;
    }

    public void setFunctions(int functions) {
        this.functions = functions;
    }

    public int getNcss() {
        return ncss;
    }

    public void setNcss(int ncss) {
        this.ncss = ncss;
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

    public int getMultiCommentLines() {
        return multiCommentLines;
    }

    public void setMultiCommentLines(int multiCommentLines) {
        this.multiCommentLines = multiCommentLines;
    }
}
