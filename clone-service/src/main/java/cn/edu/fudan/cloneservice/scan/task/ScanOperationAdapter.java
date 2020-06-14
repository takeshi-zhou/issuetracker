package cn.edu.fudan.cloneservice.scan.task;

import cn.edu.fudan.cloneservice.component.RestInterfaceManager;
import cn.edu.fudan.cloneservice.scan.dao.CloneScanDao;
import cn.edu.fudan.cloneservice.scan.domain.CloneScan;
import cn.edu.fudan.cloneservice.scan.domain.CloneScanInitialInfo;
import cn.edu.fudan.cloneservice.scan.domain.CloneScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

/**
 * @author zyh
 * @date 2020/5/25
 */
@Component
public class ScanOperationAdapter implements ScanOperation {
    private final static Logger logger = LoggerFactory.getLogger(ScanOperationAdapter.class);

    RestInterfaceManager restInterfaceManager;

    @Autowired
    public void setRestInterfaceManager(RestInterfaceManager restInterfaceManager) {
        this.restInterfaceManager = restInterfaceManager;
    }

    private CloneScanDao cloneScanDao;

    @Autowired
    public void setCloneScanDao(CloneScanDao cloneScanDao){
        this.cloneScanDao = cloneScanDao;
    }

    @Override
    public boolean isScanned(String repoId,String commitId,String type) {
        return cloneScanDao.isScanned(repoId,commitId,type);
    }

    @Override
    public CloneScanInitialInfo initialScan(String repoId, String commitId, String type) throws RuntimeException{
        //从初始化scan开始锁
        String repoPath = restInterfaceManager.getRepoPath(repoId,commitId);
        if(repoPath==null) {
            logger.error("scan initial failed ,  repo id --> {}, commit id --> {} , can't get repo path. ",repoId,commitId);
            throw new RuntimeException("request base server failed");
        }
        Date startTime = new Date();
        //新建一个Scan对象
        CloneScan cloneScan = new CloneScan();
        String uuid = UUID.randomUUID().toString();
        cloneScan.setType(type);
        cloneScan.setStartTime(startTime);
        cloneScan.setStatus("doing...");
        cloneScan.setRepoId(repoId);
        cloneScan.setCommitId(commitId);
        cloneScan.setUuid(uuid);
        cloneScanDao.insertCloneScan(cloneScan);
        return new CloneScanInitialInfo(cloneScan, repoId, repoPath, true);
    }

    @Override
    public CloneScanResult doScan(CloneScanInitialInfo cloneScanInitialInfo) {
        //等待子类的具体实现
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean updateScan(CloneScanInitialInfo cloneScanInitialInfo) {
        CloneScan cloneScan = cloneScanInitialInfo.getCloneScan();
        //更新当前Scan的状态
        cloneScan.setEndTime(new Date());
        cloneScanDao.updateCloneScan(cloneScan);
        return true;
    }
}
