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
    RepoCloneMeasureData getRepoMeasureCloneDataByRepoIdCommitId(String repo_id, String commit_id);

    RepoCloneRatio getRepoCloneRatioByRepoIdCommitId(String repo_id, String commit_id);

    List<RepoCloneInfoMonthly> getRepoCloneInfoByRepoId(String repo_id);

//    DeveloperCloneMeasureData getDeveloperMeasureCloneDataByRepoIdCommitId(String repo_id, String commit_id, String developer_name);
//
//
//    Set<PersonIdent> getDeveloperListByRepoId(String repo_id, String commit_id);
//
//    RepoCloneMeasureActiveData getCloneActive(String repo_id, String since, String until);
}
