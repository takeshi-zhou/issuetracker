package cn.edu.fudan.measureservice.domain;


public class RepoMeasure {

    private String uuid;
    private int files;
    private int ncss;
    private int classes;
    private int functions;
    private double ccn;
    private int java_docs;
    private int java_doc_lines;
    private int single_comment_lines;
    private int multi_comment_lines;
    private String commit_id;
    private String commit_time;
    private String repo_id;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public int getFunctions() {
        return functions;
    }

    public void setFunctions(int functions) {
        this.functions = functions;
    }

    public double getCcn() {
        return ccn;
    }

    public void setCcn(double ccn) {
        this.ccn = ccn;
    }

    public int getJava_docs() {
        return java_docs;
    }

    public void setJava_docs(int java_docs) {
        this.java_docs = java_docs;
    }

    public int getJava_doc_lines() {
        return java_doc_lines;
    }

    public void setJava_doc_lines(int java_doc_lines) {
        this.java_doc_lines = java_doc_lines;
    }

    public int getSingle_comment_lines() {
        return single_comment_lines;
    }

    public void setSingle_comment_lines(int single_comment_lines) {
        this.single_comment_lines = single_comment_lines;
    }

    public int getMulti_comment_lines() {
        return multi_comment_lines;
    }

    public void setMulti_comment_lines(int multi_comment_lines) {
        this.multi_comment_lines = multi_comment_lines;
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
}
