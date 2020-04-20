package cn.edu.fudan.cloneservice.domain;

import java.util.List;

/**
 * @author zyh
 * @date 2020/4/15
 */
public class CloneScanInfo {

    private String repoId;

    private List<String> commitIds;

    public String getRepoId() {
        return repoId;
    }

    public void setRepoId(String repoId) {
        this.repoId = repoId;
    }

    public List<String> getCommitIds() {
        return commitIds;
    }

    public void setCommitIds(List<String> commitIds) {
        this.commitIds = commitIds;
    }
}
