package cn.edu.fudan.cloneservice.service;


import cn.edu.fudan.cloneservice.domain.PackageInfo;

import java.util.List;

public interface CloneVisService {
    List<PackageInfo> getCommitInfoByRepoId(String repo_id, String commit_id);
}
