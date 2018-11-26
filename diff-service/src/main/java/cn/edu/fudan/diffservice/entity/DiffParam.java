package cn.edu.fudan.diffservice.entity;

public class DiffParam {
    private String commit1;
    private String commit2;
    private String filePath1;
    private String filePath2;
    private String repoid;

    public String getRepoid() {
        return repoid;
    }

    public void setRepoid(String repoid) {
        this.repoid = repoid;
    }


    public String getCommit1() {
        return commit1;
    }

    public void setCommit1(String commit1) {
        this.commit1 = commit1;
    }

    public String getCommit2() {
        return commit2;
    }

    public void setCommit2(String commit2) {
        this.commit2 = commit2;
    }

    public String getFilePath1() {
        return filePath1;
    }

    public void setFilePath1(String filePath1) {
        this.filePath1 = filePath1;
    }

    public String getFilePath2() {
        return filePath2;
    }

    public void setFilePath2(String filePath2) {
        this.filePath2 = filePath2;
    }
}
