package cn.edu.fudan.cloneservice.scan.service.impl;

import cn.edu.fudan.cloneservice.component.RestInterfaceManager;
import cn.edu.fudan.cloneservice.scan.dao.CloneLocationDao;
import cn.edu.fudan.cloneservice.scan.dao.CloneScanDao;
import cn.edu.fudan.cloneservice.scan.service.ScanService;
import cn.edu.fudan.cloneservice.scan.task.ScanTask;
import cn.edu.fudan.cloneservice.util.JGitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zyh
 * @date 2020/5/25
 */
@Service
public class ScanServiceImpl implements ScanService {

    private static final Logger logger= LoggerFactory.getLogger(ScanService.class);

    @Autowired
    private ScanTask scanTask;

    @Autowired
    private RestInterfaceManager restInterfaceManager;

    @Autowired
    private CloneLocationDao cloneLocationDao;

    @Autowired
    private CloneScanDao cloneScanDao;

    @Async("forRequest")
    @Override
    public void cloneScan(String repoId, String startCommitId) {

        logger.info("start clone scan");
        String repoPath = null;
        List<String> commitList = null;
        try {
            repoPath=restInterfaceManager.getRepoPath1(repoId);
            JGitUtil jGitHelper = new JGitUtil(repoPath);
            commitList = jGitHelper.getCommitListByBranchAndBeginCommit(startCommitId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(repoPath!=null){
                restInterfaceManager.freeRepoPath(repoId,repoPath);
            }
        }

        if(commitList != null){
            logger.info("{} commits need to scan", commitList.size());
            commitList.forEach(commitId ->{
                        scanTask.runSynchronously(repoId, commitId, "snippet");
                        scanTask.runSynchronously(repoId, commitId, "method");
                    });
        }

    }

    @Override
    public void deleteCloneScan(String repoId) {
        cloneScanDao.deleteCloneScan(repoId);
        cloneLocationDao.deleteCloneLocations(repoId);
    }


}
