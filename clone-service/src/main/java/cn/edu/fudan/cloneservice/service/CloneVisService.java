package cn.edu.fudan.cloneservice.service;


import cn.edu.fudan.cloneservice.domain.PackageInfo;

import java.util.List;

public interface CloneVisService {

    /**
     * get commit info by repo id
     *
     * @param repo_id get repo id
     * @param commit_id get commit id
     * @return List<PackageInfo>
     */
    List<PackageInfo> getCommitInfoByRepoId(String repo_id, String commit_id);
}
