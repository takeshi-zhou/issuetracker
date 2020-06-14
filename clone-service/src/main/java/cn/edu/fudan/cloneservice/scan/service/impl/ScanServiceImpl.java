package cn.edu.fudan.cloneservice.scan.service.impl;

import cn.edu.fudan.cloneservice.component.RestInterfaceManager;
import cn.edu.fudan.cloneservice.scan.dao.CloneLocationDao;
import cn.edu.fudan.cloneservice.scan.dao.CloneScanDao;
import cn.edu.fudan.cloneservice.scan.service.ScanService;
import cn.edu.fudan.cloneservice.scan.task.ScanTask;
import cn.edu.fudan.cloneservice.service.CloneMeasureService;
import cn.edu.fudan.cloneservice.thread.MultiThreadingExtractor;
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


    private ScanTask scanTask;

    @Autowired
    public void setScanTask(ScanTask scanTask) {
        this.scanTask = scanTask;
    }


    private RestInterfaceManager restInterfaceManager;

    @Autowired
    public void setRestInterfaceManager(RestInterfaceManager restInterfaceManager) {
        this.restInterfaceManager = restInterfaceManager;
    }

    private CloneLocationDao cloneLocationDao;

    @Autowired
    public void setCloneLocationDao(CloneLocationDao cloneLocationDao) {
        this.cloneLocationDao = cloneLocationDao;
    }

    private CloneScanDao cloneScanDao;

    @Autowired
    public void setCloneScanDao(CloneScanDao cloneScanDao) {
        this.cloneScanDao = cloneScanDao;
    }


    private MultiThreadingExtractor multiThreadingExtractor;

    @Autowired
    public void setMultiThreadingExtractor(MultiThreadingExtractor multiThreadingExtractor) {
        this.multiThreadingExtractor = multiThreadingExtractor;
    }

    @Async("forRequest")
    @Override
    public void cloneScan(String repoId, String startCommitId) {

        logger.info("{} -> start clone scan", Thread.currentThread().getName());

        multiThreadingExtractor.extract(repoId, startCommitId);

//        String repoPath = null;
//        List<String> commitList = null;
//        try {
//            repoPath=restInterfaceManager.getRepoPath1(repoId);
//            JGitUtil jGitHelper = new JGitUtil(repoPath);
//            commitList = jGitHelper.getCommitListByBranchAndBeginCommit(startCommitId);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if(repoPath!=null){
//                restInterfaceManager.freeRepoPath(repoId,repoPath);
//            }
//        }
//
//        int index = 0;
//        if(commitList != null){
//            logger.info("{} commits need to scan", commitList.size());
//            for(String commitId : commitList){
//                index++;
//                scanTask.runSynchronously(repoId, commitId, "snippet");
//                //只有最新的commit传进来时再启动method扫描，提高效率
//                if(index == commitList.size()){
//                    scanTask.runSynchronously(repoId, commitId, "method");
//                }
//            }
//            logger.info("***********start clone measure scan***********");
//
//        }

    }

    @Override
    public void deleteCloneScan(String repoId) {
        cloneScanDao.deleteCloneScan(repoId);
        cloneLocationDao.deleteCloneLocations(repoId);
    }


}
