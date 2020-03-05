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
    private String developer_name;
    private String developer_email;
    private int add_lines;
    private int del_lines;
    private int add_comment_lines;
    private int del_comment_lines;
    private int changed_files;
    private boolean is_merge;

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

    public String getDeveloper_name() {
        return developer_name;
    }

    public void setDeveloper_name(String developer_name) {
        this.developer_name = developer_name;
    }

    public String getDeveloper_email() {
        return developer_email;
    }

    public void setDeveloper_email(String developer_email) {
        this.developer_email = developer_email;
    }

    public int getAdd_lines() {
        return add_lines;
    }

    public void setAdd_lines(int add_lines) {
        this.add_lines = add_lines;
    }

    public int getDel_lines() {
        return del_lines;
    }

    public void setDel_lines(int del_lines) {
        this.del_lines = del_lines;
    }

    public int getAdd_comment_lines() {
        return add_comment_lines;
    }

    public void setAdd_comment_lines(int add_comment_lines) {
        this.add_comment_lines = add_comment_lines;
    }

    public int getDel_comment_lines() {
        return del_comment_lines;
    }

    public void setDel_comment_lines(int del_comment_lines) {
        this.del_comment_lines = del_comment_lines;
    }

    public int getChanged_files() {
        return changed_files;
    }

    public void setChanged_files(int changed_files) {
        this.changed_files = changed_files;
    }

    public boolean getIs_merge() {
        return is_merge;
    }

    public void setIs_merge(boolean is_merge) {
        this.is_merge = is_merge;
    }
}
