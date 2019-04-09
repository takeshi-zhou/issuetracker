package cn.edu.fudan.cloneservice.service.impl;

import cn.edu.fudan.cloneservice.dao.PackageNameDao;
import cn.edu.fudan.cloneservice.domain.PackageInfo;
import cn.edu.fudan.cloneservice.service.CloneVisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CloneVisServiceImpl implements CloneVisService {
    @Autowired
    private PackageNameDao packageNameDao;


    @Override
    public List<PackageInfo> getCommitInfoByRepoId(String repo_id, String commit_id){
        return packageNameDao.getPackageInfoByRepoIdAndCommitId(repo_id, commit_id);

    }
}
