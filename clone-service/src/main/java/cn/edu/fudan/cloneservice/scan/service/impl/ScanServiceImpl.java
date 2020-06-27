package cn.edu.fudan.cloneservice.scan.service.impl;

import cn.edu.fudan.cloneservice.component.RestInterfaceManager;
import cn.edu.fudan.cloneservice.dao.CloneMeasureDao;
import cn.edu.fudan.cloneservice.scan.dao.CloneLocationDao;
import cn.edu.fudan.cloneservice.scan.dao.CloneRepoDao;
import cn.edu.fudan.cloneservice.scan.dao.CloneScanDao;
import cn.edu.fudan.cloneservice.scan.domain.CloneRepo;
import cn.edu.fudan.cloneservice.scan.service.ScanService;
import cn.edu.fudan.cloneservice.scan.task.ScanTask;
import cn.edu.fudan.cloneservice.service.CloneMeasureService;
import cn.edu.fudan.cloneservice.thread.MultiThreadingExtractor;
import cn.edu.fudan.cloneservice.util.JGitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
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

    @Autowired
    private CloneMeasureDao cloneMeasureDao;

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

    /**
     * lookup注解修饰public 和 projected方法
     * @return
     */
    @Lookup
    protected MultiThreadingExtractor multiThreadingExtractor(){
        return null;
    }

    private CloneRepoDao cloneRepoDao;

    @Autowired
    public void setCloneRepoDao(CloneRepoDao cloneRepoDao) {
        this.cloneRepoDao = cloneRepoDao;
    }

    @Async("forRequest")
    @Override
    public void cloneScan(String repoId, String startCommitId) {

        logger.info("{} -> start clone scan", Thread.currentThread().getName());

        if(startCommitId != null){
            multiThreadingExtractor().extract(repoId, startCommitId);
        }else {
            String commitId = cloneMeasureDao.getLatestCloneLines(repoId).getCommitId();
            multiThreadingExtractor().extract(repoId, commitId);

        }

    }

    @Override
    public void deleteCloneScan(String repoId) {
        cloneScanDao.deleteCloneScan(repoId);
        cloneLocationDao.deleteCloneLocations(repoId);
    }

    @Override
    public CloneRepo getLatestCloneRepo(String repoId) {

        return cloneRepoDao.getLatestCloneRepo(repoId);
    }


}
