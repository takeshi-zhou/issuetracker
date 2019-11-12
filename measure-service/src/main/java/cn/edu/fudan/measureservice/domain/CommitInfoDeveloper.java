package cn.edu.fudan.measureservice.domain;

public class CommitInfoDeveloper  {

    //一段时间内开发者提交的commit次数和涉及到的代码变动的行数
    private String author;
    private String email;
    private int commit_counts;
    private int add;
    private int del;

    public CommitInfoDeveloper() {
    }


    public CommitInfoDeveloper(String author, String email, int commit_counts, int add, int del) {
        super();
        this.author = author;
        this.email = email;
        this.commit_counts = commit_counts;
        this.add = add;
        this.del = del;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCommit_counts() {
        return commit_counts;
    }

    public void setCommit_counts(int commit_counts) {
        this.commit_counts = commit_counts;
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
}
