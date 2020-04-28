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


    DeveloperCloneMeasureData getDeveloperMeasureCloneDataByRepoIdCommitId(String repo_id, String commit_id, String developer_name);


    Set<PersonIdent> getDeveloperListByRepoId(String repoId, String commitId);

    RepoCloneMeasureActiveData getCloneActive(String repoId, String since, String until);

    CloneMeasure getCloneMeasure(String repoId, String commitId);


}
