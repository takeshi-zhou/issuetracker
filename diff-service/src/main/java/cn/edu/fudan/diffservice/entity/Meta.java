package cn.edu.fudan.diffservice.entity;

import java.util.ArrayList;
import java.util.List;

public class Meta {
    private String project_name;
    private String author;
    private String date_time;
    private String committer;
    private String commit_hash;
    private String commit_log;
    private Object children;
    private List<String> parents;
    private List<CommitFile> files;
    private String linkPath;
    private String outputDir;
    private String projectOwnerName;


    /**
     * modified / added / removed
     */
    private List<String> actions;

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public String getLinkPath() {
        return linkPath;
    }

    public void setLinkPath(String linkPath) {
        this.linkPath = linkPath;
    }

    /**
     * 添加一个文件
     *
     * @param file
     */
    public void addFile(CommitFile file) {
        if (files == null) {
            files = new ArrayList<>();
        }

        files.add(file);
    }

    /**
     * add action    modified / added / removed
     * @return
     */
    public void addAction(String action){
        if(actions == null){
            actions = new ArrayList<>();
        }
        actions.add(action);
    }

    public List<CommitFile> getFiles() {
        return files;
    }

    public void setFiles(List<CommitFile> files) {
        this.files = files;
    }


    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getCommitter() {
        return committer;
    }

    public void setCommitter(String committer) {
        this.committer = committer;
    }

    public String getCommit_hash() {
        return commit_hash;
    }

    public void setCommit_hash(String commit_hash) {
        this.commit_hash = commit_hash;
    }

    public String getCommit_log() {
        return commit_log;
    }

    public void setCommit_log(String commit_log) {
        this.commit_log = commit_log;
    }

    public Object getChildren() {
        return children;
    }

    public void setChildren(Object children) {
        this.children = children;
    }

    public List<String> getParents() {
        return parents;
    }

    public void setParents(List<String> parents) {
        this.parents = parents;
    }

    public void addParentCommit(String commitId){
        if(this.parents == null){
            this.parents = new ArrayList<>();
        }
        this.parents.add(commitId);
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public String getProjectOwnerName() {
        return projectOwnerName;
    }

    public void setProjectOwnerName(String projectOwnerName) {
        this.projectOwnerName = projectOwnerName;
    }
}
