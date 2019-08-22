package cn.edu.fudan.cloneservice.service.impl;

import cn.edu.fudan.cloneservice.bean.CloneInstanceInfo;
import cn.edu.fudan.cloneservice.component.RestInterfaceManager;
import cn.edu.fudan.cloneservice.dao.CloneInstanceInfoDao;
import cn.edu.fudan.cloneservice.domain.DeveloperCloneMeasureData;
import cn.edu.fudan.cloneservice.domain.RepoCloneMeasureData;
import cn.edu.fudan.cloneservice.service.CloneMeasureService;
import cn.edu.fudan.cloneservice.util.JGitUtil;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.File;
import java.util.List;

/**
 * Created by njzhan
 * <p>
 * Date :2019-08-19
 * <p>
 * Description :
 * <p>
 * Version :1.0
 */

@Service
public class CloneMeasureServiceImpl implements CloneMeasureService {
    @Autowired
    CloneInstanceInfoDao cloneInstanceInfoDao;
    @Autowired
    private RestInterfaceManager restInterfaceManager;
    @Value("${repoHome}")
    private String repoHome;

    @Override
    public RepoCloneMeasureData getRepoMeasureCloneDataByRepoIdCommitId(String repo_id, String commit_id) {
        List<CloneInstanceInfo> lci =  cloneInstanceInfoDao.getCloneInsListByRepoIdAndCommitId(repo_id, commit_id);
        Integer sum = 0;
        for (CloneInstanceInfo cloneInstanceInfo: lci){
            sum += (cloneInstanceInfo.getEnd_line() - cloneInstanceInfo.getStart_line() + 1);
        }
        RepoCloneMeasureData repoCloneMeasureData = new RepoCloneMeasureData(repo_id, commit_id, sum);

        return repoCloneMeasureData;
    }

    @Override
    public DeveloperCloneMeasureData getDeveloperMeasureCloneDataByRepoIdCommitId(String repo_id, String commit_id, String developer_name) {
        String repoPath=null;
        try {
            repoPath=restInterfaceManager.getRepoPath(repo_id,commit_id);
            if(repoPath != null){
                int line_id = 0;
                PersonIdent personIdent =  JGitUtil.getPersonIdentByRepoIdAndCommitId(repoPath, commit_id, line_id);
                if(personIdent != null){
                    //get result
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(repoPath!=null)
                restInterfaceManager.freeRepoPath(repo_id,repoPath);
        }

        return null;
    }


}
