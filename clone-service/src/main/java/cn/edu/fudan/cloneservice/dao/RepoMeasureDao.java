package cn.edu.fudan.cloneservice.dao;

import cn.edu.fudan.cloneservice.domain.RepoMeasure;
import cn.edu.fudan.cloneservice.mapper.RepoMeasureMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by njzhan
 * <p>
 * Date :2019-08-28
 * <p>
 * Description :
 * <p>
 * Version :1.0
 */
@Repository
public class RepoMeasureDao {
    @Autowired
    RepoMeasureMapper repoMeasureMapper;

    public Integer getTotalLineByRepoIdCommitId(String repo_id, String commit_id){
        Integer res = 0;
        RepoMeasure repoMeasure = repoMeasureMapper.getMeasureDataByRepoIdCommitId(repo_id, commit_id);
        res = repoMeasure.getNcss();
        return res;
    }
}
