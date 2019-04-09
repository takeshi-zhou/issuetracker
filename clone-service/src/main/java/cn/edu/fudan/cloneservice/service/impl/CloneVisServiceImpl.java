package cn.edu.fudan.cloneservice.service.impl;

import cn.edu.fudan.cloneservice.dao.PackageNameDao;
import cn.edu.fudan.cloneservice.domain.PackageInfo;
import cn.edu.fudan.cloneservice.service.CloneVisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CloneVisServiceImpl implements CloneVisService {
    @Autowired
    private PackageNameDao packageNameDao;


    @Override
    public String getCommitInfoByRepoId(String repo_id){
//        packageNameDao.insertTest(new PackageInfo("test", repo_id, "ss", "ss", 1, 1));
        return packageNameDao.selectTest(repo_id, "556b393e1f68462722faa4564daea792e2607cb1");

    }
}
