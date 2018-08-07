package cn.edu.fudan.projectmanager.domain;

public class NeedDownload {


    private String projectId;
    private String url;


    public NeedDownload() {
    }

    public NeedDownload(String projectId, String url) {
        this.projectId = projectId;
        this.url = url;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
