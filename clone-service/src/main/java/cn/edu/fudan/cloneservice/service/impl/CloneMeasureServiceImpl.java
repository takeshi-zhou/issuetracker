package cn.edu.fudan.cloneservice.service.impl;

import cn.edu.fudan.cloneservice.component.RestInterfaceManager;
import cn.edu.fudan.cloneservice.dao.*;
import cn.edu.fudan.cloneservice.domain.CloneInfo;
import cn.edu.fudan.cloneservice.domain.CloneMeasure;
import cn.edu.fudan.cloneservice.domain.CloneMessage;
import cn.edu.fudan.cloneservice.domain.CommitChange;
import cn.edu.fudan.cloneservice.scan.dao.CloneLocationDao;
import cn.edu.fudan.cloneservice.scan.domain.CloneLocation;
import cn.edu.fudan.cloneservice.service.CloneMeasureService;
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
            commitIds1 = JGitUtil.getCommitList(repoPath, start, end, developer);
            commitIds2 = JGitUtil.getCommitList(repoPath, start, end, null);
        }finally {
            if(repoPath!=null){
                restInterfaceManager.freeRepoPath(repoId,repoPath);
            }
        }
        List<CloneMeasure> cloneMeasures = cloneMeasureDao.getCloneMeasures(repoId);
        for(CloneMeasure cloneMeasure1 : cloneMeasures){
            for(String commitId : commitIds1){
                if(cloneMeasure1.getCommitId().equals(commitId)){
                    newCloneLines += cloneMeasure1.getNewCloneLines();
                    selfCloneLines += cloneMeasure1.getSelfCloneLines();
                    //消除clone行数,只计算消除
                    if(cloneMeasure1.getCloneLines() > 0 && cloneMeasure1.getPreCloneLines() > cloneMeasure1.getCloneLines()){
                        deleteCloneLines += cloneMeasure1.getPreCloneLines() - cloneMeasure1.getCloneLines();
                    }
                }
            }
        }
        //计算这个时间段内所有的开发者消除的clone行数
        for(CloneMeasure cloneMeasure1 : cloneMeasures){
            for(String commitId : commitIds2){
                if(cloneMeasure1.getCommitId().equals(commitId)){
                    //消除clone行数,只计算消除
                    if(cloneMeasure1.getCloneLines() > 0 && cloneMeasure1.getPreCloneLines() > cloneMeasure1.getCloneLines()){
                        allDeleteCloneLines += cloneMeasure1.getPreCloneLines() - cloneMeasure1.getCloneLines();
                    }
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

    private int getCloneLines(String repoId, String commitId){

        int cloneLinesWithOutTest = 0;
        if(commitId != null){
            int oneLocationCloneLines;

            String className;
            String fullName;
            List<CloneLocation> cloneLocations = cloneLocationDao.getCloneLocations(repoId, commitId);
            for(CloneLocation location: cloneLocations){
                className = location.getFilePath().toLowerCase();
                fullName = className.substring(className.lastIndexOf("/") + 1);
                if(className.contains("/test/") || fullName.startsWith("test") || fullName.endsWith("test.java") || fullName.endsWith("tests.java")){
                    continue;
                }
                String [] ss = location.getCloneLines().split(",");
                oneLocationCloneLines = Integer.parseInt(ss[1]) - Integer.parseInt(ss[0]) + 1;
                cloneLinesWithOutTest += oneLocationCloneLines;

            }
            logger.info("repoId:{} - commitId:{}--->cloneLines:{}",repoId, commitId, cloneLinesWithOutTest);
            return cloneLinesWithOutTest;
        }

        return -1;
    }

    @Override
    public CloneMeasure insertCloneMeasure(String repoId, String commitId){
        CloneMeasure cloneMeasure = new CloneMeasure();
        int increasedCloneLines = 0;
        int increasedSameCloneLines = 0;
        int increasedLines;
        int preCloneLines;
        int currentCloneLines;
        String repoPath=null;
        Map<String, String> map;
        try {
            repoPath=restInterfaceManager.getRepoPath(repoId,commitId);
            CommitChange commitChange = JGitUtil.getNewlyIncreasedLines(repoPath, commitId);
            String preCommitId = JGitUtil.getPreCommitId(repoPath, commitId);
            preCloneLines = getCloneLines(repoId, preCommitId);
            currentCloneLines = getCloneLines(repoId, commitId);
            map = commitChange.getAddMap();
            increasedLines = commitChange.getAddLines();
            //此处修改为最新的clone location
            List<CloneLocation> cloneLocations = cloneLocationDao.getCloneLocations(repoId, commitId);
            Map<String, List<CloneLocation>> cloneLocationMap = new HashMap<>(512);
            logger.info("cloneLocation init start!");
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
            logger.info("cloneLocation init success!");
            //key记录repoPath, value记录新增且是clone的行号
            Map<String, String> addCloneLocationMap = new HashMap<>(512);
            //key记录repoPath, value记录新增且是self clone的行号
            Map<String, String> selfCloneLocationMap = new HashMap<>(512);
            //遍历此版本所有的clone location
            for(CloneLocation cloneLocation : cloneLocations){
                String cloneLines = cloneLocation.getCloneLines();
                int startLine = Integer.parseInt(cloneLines.split(",")[0]);
                int endLine = Integer.parseInt(cloneLines.split(",")[1]);
                for(String filePath: map.keySet()){
                    if(filePath.equals(cloneLocation.getFilePath())){

                        String[] lines = map.get(filePath).split(",");

                        for(int i = 0; i < lines.length; i++){
                            if(Integer.parseInt(lines[i]) >= startLine && Integer.parseInt(lines[i]) <= endLine){
                                logger.info("find increasedCloneLines:"+Integer.parseInt(lines[i])+"-"+filePath);
                                List<CloneLocation> list = cloneLocationMap.get(cloneLocation.getCategory());
                                String category = cloneLocation.getCategory();
                                list.remove(cloneLocation);
                                for(CloneLocation cloneLocation1 : list){
                                    String filePath1 = cloneLocation1.getFilePath();
                                    String cloneLines1 = cloneLocation1.getCloneLines();
                                    if(JGitUtil.isSameDeveloperClone(repoPath, commitId, filePath1, cloneLines1)){
                                        increasedSameCloneLines++;
                                        if(selfCloneLocationMap.containsKey(category + ":" +filePath)){
                                            String s1 = selfCloneLocationMap.get(category + ":" +filePath) + "," + lines[i];
                                            selfCloneLocationMap.put(category + ":" +filePath, s1);
                                        }else {
                                            selfCloneLocationMap.put(category + ":" +filePath, lines[i]);
                                        }
                                        break;
                                    }
                                }
                                increasedCloneLines++;
                                if(addCloneLocationMap.containsKey(category + ":" +filePath)){
                                    String s1 = addCloneLocationMap.get(category + ":" +filePath) + "," + lines[i];
                                    addCloneLocationMap.put(category + ":" +filePath, s1);
                                }else {
                                    addCloneLocationMap.put(category + ":" +filePath, lines[i]);
                                }
                            }
                        }
                        break;
                    }
                }
            }

            //插入cloneInfo
            List<CloneInfo> cloneInfoList = getCloneInfoList(repoId, commitId, addCloneLocationMap, selfCloneLocationMap);

            String uuid = UUID.randomUUID().toString();
            cloneMeasure.setUuid(uuid);
            cloneMeasure.setCommitId(commitId);
            cloneMeasure.setRepoId(repoId);
            cloneMeasure.setNewCloneLines(increasedCloneLines);
            cloneMeasure.setAddLines(increasedLines);
            cloneMeasure.setSelfCloneLines(increasedSameCloneLines);
            cloneMeasure.setPreCloneLines(preCloneLines);
            cloneMeasure.setCloneLines(currentCloneLines);
            cloneMeasureDao.insertCloneMeasure(cloneMeasure);
            logger.info("cloneInfoList size : {}", cloneInfoList.size());
            if(cloneInfoList.size() > 0){
                cloneInfoDao.insertCloneInfo(cloneInfoList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
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
            CloneInfo cloneInfo = new CloneInfo(uuid, repoId, commitId, filePath, addCloneLocationMap.get(clone),selfCloneLines, type);
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


}
