package cn.edu.fudan.projectmanager.domain;

public class NeedDownload {


    private String projectId;
    private String URL;


    public NeedDownload() {
    }

    public NeedDownload(String projectId, String URL) {
        this.projectId = projectId;
        this.URL = URL;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
