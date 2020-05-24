package cn.edu.fudan.cloneservice.domain;

/**
 * @author zyh
 * @date 2020/5/24
 */
public class CommitInfo {

    private String author;
    private String email;
    private int commit_counts;
    private int add;
    private int del;
    private int changed_files;

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

    public int getChanged_files() {
        return changed_files;
    }

    public void setChanged_files(int changed_files) {
        this.changed_files = changed_files;
    }
}
