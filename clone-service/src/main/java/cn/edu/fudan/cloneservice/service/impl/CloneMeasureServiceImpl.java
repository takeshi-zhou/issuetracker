package cn.edu.fudan.cloneservice.service.impl;

import cn.edu.fudan.cloneservice.component.RestInterfaceManager;
import cn.edu.fudan.cloneservice.dao.*;
import cn.edu.fudan.cloneservice.domain.CloneInfo;
import cn.edu.fudan.cloneservice.domain.CloneMeasure;
import cn.edu.fudan.cloneservice.domain.CloneMessage;
import cn.edu.fudan.cloneservice.domain.CommitChange;
import cn.edu.fudan.cloneservice.lock.CloneMeasureLock;
import cn.edu.fudan.cloneservice.scan.dao.CloneLocationDao;
import cn.edu.fudan.cloneservice.scan.domain.CloneLocation;
import cn.edu.fudan.cloneservice.service.CloneMeasureService;
import cn.edu.fudan.cloneservice.thread.ForkJoinRecursiveTask;
import cn.edu.fudan.cloneservice.util.ComputeUtil;
import cn.edu.fudan.cloneservice.util.JGitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CloneMeasureServiceImpl implements CloneMeasureService {

    private static Logger logger = LoggerFactory.getLogger(CloneMeasureServiceImpl.class);

    @Autowired
    private RestInterfaceManager restInterfaceManager;

    @Autowired
    private CloneMeasureDao cloneMeasureDao;

    @Autowired
    private CloneInfoDao cloneInfoDao;

    @Value("${repoHome}")
    private String repoHome;

    @Autowired
    private CloneLocationDao cloneLocationDao;

    @Autowired
    private CloneMeasureLock cloneMeasureLock;

    @Autowired
    private ForkJoinRecursiveTask forkJoinRecursiveTask;


    @Override
    public CloneMessage getCloneMeasure(String repoId, String developer, String start, String end){
        CloneMessage cloneMessage = new CloneMessage();
        String repoPath = null;
        List<String> commitIds1;
        List<String> commitIds2;
        int newCloneLines = 0;
        int selfCloneLines = 0;
        int deleteCloneLines = 0;
        int allDeleteCloneLines = 0;
        try {
            repoPath = restInterfaceManager.getRepoPath1(repoId);
            JGitUtil jGitHelper = new JGitUtil(repoPath);
            commitIds1 = jGitHelper.getCommitList(repoPath, start, end, developer);
            commitIds2 = jGitHelper.getCommitList(repoPath, start, end, null);
        }finally {
            if(repoPath!=null){
                restInterfaceManager.freeRepoPath(repoId,repoPath);
            }
        }
        List<CloneMeasure> cloneMeasures = cloneMeasureDao.getCloneMeasures(repoId);

        int preCloneLines = 0;
        for(String commitId : commitIds2){
            for(CloneMeasure cloneMeasure1 : cloneMeasures){
                //计算个人
                if(cloneMeasure1.getCommitId().equals(commitId) && commitIds1.contains(commitId)){
                    newCloneLines += cloneMeasure1.getNewCloneLines();
                    selfCloneLines += cloneMeasure1.getSelfCloneLines();
                    if(preCloneLines > cloneMeasure1.getCloneLines()){
                        deleteCloneLines += preCloneLines - cloneMeasure1.getCloneLines();
                    }
                }
                //计算全体
                if(cloneMeasure1.getCommitId().equals(commitId)){
                    //消除clone行数,只计算消除
                    if(preCloneLines > cloneMeasure1.getCloneLines()){
                        allDeleteCloneLines += preCloneLines - cloneMeasure1.getCloneLines();
                    }
                    preCloneLines = cloneMeasure1.getCloneLines();
                    break;
                }
            }
        }
        int addLines = restInterfaceManager.getAddLines(repoId, start, end, developer);
        cloneMessage.setIncreasedCloneLines(newCloneLines+"");
        cloneMessage.setSelfIncreasedCloneLines(selfCloneLines+"");
        cloneMessage.setIncreasedCloneLinesRate(newCloneLines+"/"+addLines);
        cloneMessage.setEliminateCloneLines(deleteCloneLines+"");
        cloneMessage.setAllEliminateCloneLines(allDeleteCloneLines+"");

        return cloneMessage;
    }

    @Override
    public void deleteCloneMeasureByRepoId(String repoId) {
        cloneMeasureDao.deleteCloneMeasureByRepoId(repoId);
        cloneInfoDao.deleteCloneInfo(repoId);
    }

    /**
     * 获取某个版本的clone行数
     * 计算的方式有待商榷
     * @param repoId repo id
     * @param commitId commit id
     * @return 行数
     */
    private int getCloneLines(String repoId, String commitId){

        int cloneLinesWithOutTest = 0;
        if(commitId != null){
            List<CloneLocation> cloneLocations = cloneLocationDao.getCloneLocations(repoId, commitId);
            Map<String, List<String>> locationMap = new HashMap<>(512);
            for(CloneLocation location: cloneLocations){
                String[] nums = location.getNum().split(",");
                for(String line : nums){
                    locationMap = ComputeUtil.putNewNum(locationMap, line, location.getFilePath());
                }
            }
            logger.info("repoId:{} - commitId:{}--->cloneLines:{}",repoId, commitId, cloneLinesWithOutTest);
            return ComputeUtil.getCloneLines(locationMap);
        }

        return -1;
    }

    @Override
    public CloneMeasure insertCloneMeasure(String repoId, String commitId){
        //如果数据库中有对应的信息则直接返回
        if(cloneMeasureDao.getCloneMeasureCount(repoId, commitId) > 0){
            return null;
        }
        CloneMeasure cloneMeasure = new CloneMeasure();
        int increasedLines;
        int currentCloneLines;
        String repoPath=null;
        Map<String, String> map;
        //对repoPath加锁
        cloneMeasureLock.lock();
        try {
            repoPath=restInterfaceManager.getRepoPath(repoId,commitId);
            CommitChange commitChange = JGitUtil.getNewlyIncreasedLines(repoPath, commitId);
            JGitUtil jGitHelper = new JGitUtil(repoPath);
            Date commitTime = new Date(jGitHelper.getLongCommitTime(commitId));
            currentCloneLines = getCloneLines(repoId, commitId);
            map = commitChange.getAddMap();
            increasedLines = commitChange.getAddLines();
            List<CloneLocation> cloneLocations = cloneLocationDao.getCloneLocations(repoId, commitId);
            logger.info("location list size : {}", cloneLocations.size());
            Map<String, List<CloneLocation>> cloneLocationMap = new HashMap<>(512);
            logger.info("clone measure {} -> cloneLocation init start!", Thread.currentThread().getName());
            //初始化
            for(CloneLocation cloneLocation : cloneLocations){
                String category = cloneLocation.getCategory();
                if(cloneLocationMap.containsKey(category)){
                    cloneLocationMap.get(category).add(cloneLocation);
                }else {
                    List<CloneLocation> locations = new ArrayList<>();
                    locations.add(cloneLocation);
                    cloneLocationMap.put(category, locations);
                }
            }
            //key记录repoPath, value记录新增且是clone的行号
            Map<String, String> addCloneLocationMap;
            //key记录repoPath, value记录新增且是self clone的行号
            Map<String, String> selfCloneLocationMap;

            cloneMeasure = forkJoinRecursiveTask.extract(repoId, commitId, repoPath, cloneLocations, cloneLocationMap, map);
            addCloneLocationMap = cloneMeasure.getAddCloneLocationMap();
            selfCloneLocationMap = cloneMeasure.getSelfCloneLocationMap();
            //插入cloneInfo
            List<CloneInfo> cloneInfoList = getCloneInfoList(repoId, commitId, addCloneLocationMap, selfCloneLocationMap);

            cloneMeasure.setAddLines(increasedLines);
            cloneMeasure.setCloneLines(currentCloneLines);
            cloneMeasure.setCommitTime(commitTime);
            cloneMeasureDao.insertCloneMeasure(cloneMeasure);
            logger.info("{} -> cloneInfoList size : {}", Thread.currentThread().getName(), cloneInfoList.size());
            if(cloneInfoList.size() > 0){
                cloneInfoDao.insertCloneInfo(cloneInfoList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cloneMeasureLock.unlock();
            if(repoPath!=null){
                restInterfaceManager.freeRepoPath(repoId,repoPath);
            }
        }

        return cloneMeasure;
    }

    private List<CloneInfo> getCloneInfoList(String repoId, String commitId, Map<String, String> addCloneLocationMap, Map<String, String> selfCloneLocationMap){
        List<CloneInfo> cloneInfoList = new ArrayList<>();
        for(String clone : addCloneLocationMap.keySet()){
            String type = clone.substring(0, clone.indexOf(":"));
            String filePath = clone.substring(clone.indexOf(":") + 1);
            String uuid = UUID.randomUUID().toString();
            String selfCloneLines = null;
            if(selfCloneLocationMap.containsKey(clone)){
                selfCloneLines = selfCloneLocationMap.get(clone);
            }
            CloneInfo cloneInfo = new CloneInfo(uuid, repoId, commitId, filePath, addCloneLocationMap.get(clone), selfCloneLines, type);
            cloneInfoList.add(cloneInfo);
        }

        return cloneInfoList;
    }

    @Async("forRequest")
    @Override
    public void scanCloneMeasure(String repoId, String startCommitId){
        logger.info("start clone measure scan");
        String repoPath = null;
        List<String> commitList = null;
        try {
            repoPath=restInterfaceManager.getRepoPath1(repoId);
            logger.info("repoPath:{}", repoPath);
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
            logger.info("need scan {} commits",commitList.size());
            for(String commitId : commitList){
                insertCloneMeasure(repoId, commitId);
            }

        }

    }

    @Override
    public CloneMeasure getLatestCloneMeasure(String repoId) {

        return cloneMeasureDao.getLatestCloneLines(repoId);
    }


}
