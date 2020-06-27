package cn.edu.fudan.measureservice.service.impl;

import cn.edu.fudan.measureservice.annotation.RepoResource;
import cn.edu.fudan.measureservice.component.RestInterfaceManager;
import cn.edu.fudan.measureservice.domain.core.MeasureScan;
import cn.edu.fudan.measureservice.domain.dto.RepoResourceDTO;
import cn.edu.fudan.measureservice.mapper.RepoMeasureMapper;
import cn.edu.fudan.measureservice.util.JGitHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * description: 度量扫描服务
 *
 * @author fancying
 * create: 2020-06-23 17:08
 **/
@Slf4j
@Service("measure2")
public class MeasureScanServiceImpl{

    private RestInterfaceManager rest;
    private RepoMeasureMapper repoMeasureMapper;

//    @RepoResource
//    @Async("taskExecutor")
//    public boolean scan(RepoResourceDTO repoResource, String branch, String beginCommit, String toolName) {
//        String repoPath = repoResource.getRepoPath();
//        String repoId = repoResource.getRepoId();
//
//        //1. 判断beginCommit是否为空,为空则表示此次为update，不为空表示此次为第一次扫描
//        // 若是update，则获取最近一次扫描的commit_id，作为本次扫描的起始点
//        if (StringUtils.isEmpty(beginCommit)){
//            beginCommit = repoMeasureMapper.getLastScannedCommitId(repoId);
//        }
//
//        if (StringUtils.isEmpty(repoPath)){
//            log.error("repoId:[{}] path is empty", repoId);
//            return false;
//        }
//
//        JGitHelper jGitHelper = new JGitHelper(repoPath);
//        // 获取从 beginCommit 开始的 commit list 列表
//        List<String> commitList = jGitHelper.getCommitListByBranchAndBeginCommit(branch, beginCommit);
//        //初始化本次扫描状态信息
//        MeasureScan measureScan = initMeasureScan(repoId, toolName, commitList.get(0), commitList.get(0), commitList.size());
//        Date startScanTime = measureScan.getStartScanTime();
//
//        // 遍历列表 进行扫描
//        for (String commit : commitList) {
//            String commitTime = jGitHelper.getCommitTime(commit);
//            log.info("Start to scan measure info: repoId is {} commit is {}", repoId, commit );
//            saveMeasureData(repoId, commit, commitTime, repoPath);
//            //更新本次扫描状态信息
//            Date currentTime = new Date();
//            int scanTime = (int) (currentTime.getTime()-startScanTime.getTime()) / 1000;
//            String status = "scanning";
//            if (i == commitList.size()-1){//扫描到最后一个commit
//                status = "complete";
//            }
//            updateMeasureScan(measureScan,commitList.get(i),i+1,scanTime,status,currentTime);
//        }
//        log.info("Measure scan complete!");
//        return true;
//    }


    @Async("taskExecutor")
    public boolean update(String repoId, String branch, String beginCommit, String toolName) {
        // 1 先判断是否在扫描中 是则更新扫描的commit列表


        return true;
    }

    @Autowired
    public void setRest(RestInterfaceManager rest) {
        this.rest = rest;
    }

    @Autowired
    public void setRepoMeasureMapper(RepoMeasureMapper repoMeasureMapper) {
        this.repoMeasureMapper = repoMeasureMapper;
    }

}