package cn.edu.fudan.cloneservice.service;

import cn.edu.fudan.cloneservice.domain.*;
import org.eclipse.jgit.lib.PersonIdent;

import java.util.List;
import java.util.Set;

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

    /**
     * get repo measure clone data by repo id commit id
     *
     * @param repo_id get user repo id
     * @param commit_id get user commit id
     * @return RepoCloneMeasureData
     */
    RepoCloneMeasureData getRepoMeasureCloneDataByRepoIdCommitId(String repo_id, String commit_id);

    /**
     * get repo clone ratio by repo id commit id
     *
     * @param repo_id get user repo id
     * @param commit_id get user commit id
     * @return RepoCloneRatio
     */
    RepoCloneRatio getRepoCloneRatioByRepoIdCommitId(String repo_id, String commit_id);

    /**
     * get repo clone info by repo id
     *
     * @param repo_id get user repo id
     * @return List<RepoCloneInfoMonthly>
     */
    List<RepoCloneInfoMonthly> getRepoCloneInfoByRepoId(String repo_id);

//    DeveloperCloneMeasureData getDeveloperMeasureCloneDataByRepoIdCommitId(String repo_id, String commit_id, String developer_name);
//
//
//    Set<PersonIdent> getDeveloperListByRepoId(String repo_id, String commit_id);
//
//    RepoCloneMeasureActiveData getCloneActive(String repo_id, String since, String until);
}
