package cn.edu.fudan.cloneservice.service;

import cn.edu.fudan.cloneservice.domain.DeveloperCloneMeasureData;
import cn.edu.fudan.cloneservice.domain.RepoCloneMeasureData;

/**
 * Created by njzhan
 * <p>
 * Date :2019-08-19
 * <p>
 * Description :
 * <p>
 * Version :1.0
 */
public interface CloneMeasureService {
    RepoCloneMeasureData getRepoMeasureCloneDataByRepoIdCommitId(String repo_id, String commit_id);

    DeveloperCloneMeasureData getDeveloperMeasureCloneDataByRepoIdCommitId(String repo_id, String commit_id, String developer_name);
}
