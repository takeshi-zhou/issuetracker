package cn.edu.fudan.cloneservice.dao;

import cn.edu.fudan.cloneservice.domain.CloneInfo;
import cn.edu.fudan.cloneservice.mapper.CloneInfoMapper;
import cn.edu.fudan.cloneservice.mapper.CloneInstanceInfoMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class CloneInfoDao {
    CloneInfoMapper cloneInfoMapper;

    public CloneInfoDao(CloneInfoMapper cloneInfoMapper) {
        this.cloneInfoMapper = cloneInfoMapper;
    }

    public List<CloneInfo> getCloneInfoByRepoIdAndCommitId(String repo_id, String commit_id){
        List<CloneInfo> lci = new ArrayList<>();
        try{
           lci = cloneInfoMapper.selectCloneInfoByCommitIdAndRepoId(repo_id, commit_id);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return lci;

    }

    public void updateCloneInfoByRepoIdAndCommitId(String repo_id, String commit_id, Map<String, Integer> map_clone_dis){
        for(String key: map_clone_dis.keySet()){

                cloneInfoMapper.updateCloneInfoByCommitIdAndRepoId(repo_id, commit_id, key, map_clone_dis.get(key));


        }
        System.out.println("happy!");

    }
}
