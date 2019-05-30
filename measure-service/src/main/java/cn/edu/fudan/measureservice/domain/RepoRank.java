package cn.edu.fudan.measureservice.domain;

public class RepoRank {

    private String reoName;
    private Integer commitCount;

    public String getReoName() {
        return reoName;
    }

    public void setReoName(String reoName) {
        this.reoName = reoName;
    }

    public int getCommitCount() {
        return commitCount;
    }

    public void setCommitCount(int commitCount) {
        this.commitCount = commitCount;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.reoName.hashCode();
        result = 31 * result + this.commitCount.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof RepoRank)) return false;
        RepoRank repoRank=(RepoRank)obj;
        return this.reoName.equals(repoRank.reoName)&&this.commitCount.equals(repoRank.commitCount);
    }
}
