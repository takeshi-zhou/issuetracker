package cn.edu.fudan.measureservice.domain;

public class CommitInfo {

    private String author;
    private int commit;
    private int add;
    private int del;

    public CommitInfo() {
    }


    public CommitInfo(String author, int commit, int add, int del) {
        super();
        this.author = author;
        this.commit = commit;
        this.add = add;
        this.del = del;
    }


    public String getAuthor() {
        return author;
    }


    public void setAuthor(String author) {
        this.author = author;
    }


    public int getCommit() {
        return commit;
    }


    public void setCommit(int commit) {
        this.commit = commit;
    }


    public int getAdd() {
        return add;
    }


    public void setAdd(int add) {
        this.add = add;
    }


    public int getDel() {
        return del;
    }


    public void setDel(int del) {
        this.del = del;
    }


    @Override
    public String toString() {
        return "CommitInfo [author=" + author + ", commit=" + commit + ", add=" + add + ", del=" + del + "]";
    }



}
