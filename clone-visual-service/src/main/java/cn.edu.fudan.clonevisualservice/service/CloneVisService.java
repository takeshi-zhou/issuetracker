package cn.edu.fudan.clonevisualservice.service;

public interface CloneVisService {
    Object getCommitInfoByRepoId(String repo_id);
    void insertTest(String repo_id);
}
