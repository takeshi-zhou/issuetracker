package cn.edu.fudan.clonevisualservice.service.impl;

import cn.edu.fudan.clonevisualservice.dao.CommitInfoDao;
import cn.edu.fudan.clonevisualservice.domain.PackageInfo;
import cn.edu.fudan.clonevisualservice.service.CloneVisService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class CloneVisServiceImpl implements CloneVisService {
    @Autowired
    private CommitInfoDao commitInfoDao;

    @Override
    public void insertTest(String repo_id){
        commitInfoDao.testInsert(repo_id, "commit");
    }

    @Override
    public Object getCommitInfoByRepoId(String repo_id){
        Map<String, Map> res_whole_map = new HashMap<>();
        commitInfoDao.testInsert(repo_id, "test");
//        List<String> lcl =  commitInfoDao.getCommitListByRepoId(repo_id);
//        for(String commit_id : lcl){
//          Map<String, Map> commit_map = new HashMap<>();
//          List<PackageInfo> lpi =   commitInfoDao.getPackageInfoByRepoIdAndCommitId(repo_id, commit_id);
//          for(PackageInfo pi:lpi){
//                Map<Integer, Integer> package_map = new HashMap<>();
//                package_map.put(pi.getMethod_num(), pi.getClone_ins_num());
//                commit_map.put(pi.getPackage_name(), package_map);
//          }
//            res_whole_map.put(commit_id, commit_map);
//        }
//        return res_whole_map;

        return null;
    }
}
