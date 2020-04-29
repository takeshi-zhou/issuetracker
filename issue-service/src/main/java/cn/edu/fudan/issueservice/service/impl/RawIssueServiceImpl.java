package cn.edu.fudan.issueservice.service.impl;

import cn.edu.fudan.issueservice.component.RestInterfaceManager;
import cn.edu.fudan.issueservice.dao.LocationDao;
import cn.edu.fudan.issueservice.dao.RawIssueDao;
import cn.edu.fudan.issueservice.domain.Location;
import cn.edu.fudan.issueservice.domain.RawIssue;
import cn.edu.fudan.issueservice.domain.RawIssueStatus;
import cn.edu.fudan.issueservice.service.RawIssueService;
import cn.edu.fudan.issueservice.util.JGitHelper;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author WZY
 * @version 1.0
 **/
@Slf4j
@Service
public class RawIssueServiceImpl implements RawIssueService {
    private Logger logger = LoggerFactory.getLogger(RawIssueServiceImpl.class);


    private RestInterfaceManager restInterfaceManager;

    @Autowired
    public void setRestInterfaceManager(RestInterfaceManager restInterfaceManager) {
        this.restInterfaceManager = restInterfaceManager;
    }

    private RawIssueDao rawIssueDao;

    @Autowired
    public void setRawIssueDao(RawIssueDao rawIssueDao) {
        this.rawIssueDao = rawIssueDao;
    }

    private LocationDao locationDao;

    @Autowired
    public void setLocationDao(LocationDao locationDao) {
        this.locationDao = locationDao;
    }

    /**
     * 插入rawIssue列表，同时会插入里面包含的locations
     *
     * @param list rawIssue的列表，里面包含了locations
     * @author WZY
     */
    @Override
    public void insertRawIssueList(List<RawIssue> list) {
        List<Location> locations = new ArrayList<>();
        for (RawIssue rawIssue : list) {
            locations.addAll(rawIssue.getLocations());
        }
        int rawIssueSize = list.size();
        RawIssue[] rawIssueArray = list.toArray(new RawIssue[rawIssueSize]);

        for(int i = 0 ; i < rawIssueSize; ){
            List<RawIssue> cutOfRawIssue = new ArrayList<>();
            if(rawIssueSize > i + 10){

                for(int j = i ; j < i+10 ; j++){
                    cutOfRawIssue.add(rawIssueArray[j]);
                }
                rawIssueDao.insertRawIssueList(cutOfRawIssue);
            }else{

                for(int j = i ; j < rawIssueSize ; j++){
                    cutOfRawIssue.add(rawIssueArray[j]);
                }
                rawIssueDao.insertRawIssueList(cutOfRawIssue);
            }
            i += 10;
        }

        int locationsSize = locations.size();
        Location[] locationsArray = locations.toArray(new Location[locationsSize]);

        for(int i = 0 ; i < locationsSize; ){
            List<Location> cutOfLocation = new ArrayList<>();
            if(locationsSize > i + 10){

                for(int j = i ; j < i+10 ; j++){
                    cutOfLocation.add(locationsArray[j]);
                }
                locationDao.insertLocationList(cutOfLocation);
            }else{

                for(int j = i ; j < rawIssueSize ; j++){
                    cutOfLocation.add(locationsArray[j]);
                }
                locationDao.insertLocationList(cutOfLocation);
            }
            i += 10;
        }

//        rawIssueDao.insertRawIssueList(list);
//        locationDao.insertLocationList(locations);
    }

    /**
     * 删除rawIssue以及对应的locations
     *
     * @param repoId 项目的id
     * @author WZY
     */
    @Transactional
    @Override
    public void deleteRawIssueByRepoIdAndCategory(String repoId,String category) {
        locationDao.deleteLocationByRepoIdAndCategory(repoId,category);
        rawIssueDao.deleteRawIssueByRepoIdAndCategory(repoId,category);
    }

    @Override
    public void batchUpdateIssueId(List<RawIssue> list) {
        rawIssueDao.batchUpdateIssueIdAndStatus(list);
    }

    @Override
    public List<RawIssue> getRawIssueByCommitIDAndCategory(String repo_id,String commit_id,String category) {
        return rawIssueDao.getRawIssueByCommitIDAndCategory(repo_id,commit_id,category);
    }

    @Override
    public List<RawIssue> getRawIssueByIssueId(String issueId) {
        return rawIssueDao.getRawIssueByIssueId(issueId);
    }

    @Override
    public Object getCode(String project_id, String commit_id, String file_path) {
//        file_path=file_path.substring(file_path.indexOf("/")+1);
        Map<String, Object> result = new HashMap<>();
        String repo_id =restInterfaceManager.getRepoIdOfProject(project_id);
        String repoHome=null;
        try{
            JSONObject response = restInterfaceManager.getRepoPath(repo_id,commit_id).getJSONObject("data");
            logger.info(response.toJSONString());
            if (response != null && response.getString("status").equals("Successful")) {
                repoHome=response.getString("content");
                logger.info("repoHome -> {}" ,repoHome);
                result.put("code", getFileContent(repoHome+"/"+file_path));
            } else {
                result.put("code", "");
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }finally {
            if(repoHome!=null){
                JSONObject response =restInterfaceManager.freeRepoPath(repo_id,repoHome);
                if (response != null && response.getJSONObject("data").getString("status").equals("Successful")) {
                    logger.info("{} free success",repoHome);
                } else {
                    logger.info("{} free failed",repoHome);
                }
            }

        }
        return result;
    }

    private String getFileContent(String filePath){
        StringBuilder code = new StringBuilder();
        String s = "";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            while ((s = bufferedReader.readLine()) != null) {
                code.append(s);
                code.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code.toString();
    }

    @Override
    public List<Location> getLocationsByRawIssueId(String raw_issue_id) {
        return locationDao.getLocations(raw_issue_id);
    }

    @Override
    public List<RawIssue> getRawIssueByCommitAndFile(String repo_id,String commit_id, String category, String filePath) {

        return rawIssueDao.getRawIssueByCommitIDAndFile(repo_id,commit_id, category, filePath);
    }

    @Override
    public Object getRawIssueList(String issue_id,Integer page,Integer size,String status){
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        List<String> statusList = new ArrayList<>();
        if(status != null){
            String[] statusArray =  status.split(",");
            for(String statusJudge : statusArray){
                RawIssueStatus rawIssueStatus = RawIssueStatus.getStatusByName(statusJudge);
                if(rawIssueStatus == null ){
                    return statusJudge + " is wrong, please input add ,changed or solved.";
                }
                statusList.add(statusJudge);
            }
            param.put("list",statusList);
        }
        param.put("issue_id",issue_id);
        param.put("page",page);
        param.put("size",size);
        /*获取总页数*/
        int count = rawIssueDao.getNumberOfRawIssuesByIssueIdAndStatus(issue_id,statusList);
        param.put("start", (page - 1) * size);
        result.put("totalPage", count % size == 0 ? count / size : count / size + 1);
        result.put("totalCount", count);
        List<RawIssue> rawIssues = rawIssueDao.getRawIssueListByIssueId(param);
        if(rawIssues.size() != 0){
            JSONObject repoPathJson = null;
            String repoPath = null;
            JGitHelper jGitHelper = null;
            String repoId = rawIssues.get(0).getRepo_id();
            String commitId = rawIssues.get(rawIssues.size()-1).getCommit_id();
            try{
                repoPathJson = restInterfaceManager.getRepoPath(repoId,commitId);
                if(repoPathJson == null){
                    throw new RuntimeException("can not get repo path");
                }
                repoPath = repoPathJson.getJSONObject("data").getString("content");
                if(repoPath == null){
                    throw new RuntimeException("can not get repo path");
                }
                jGitHelper = new JGitHelper(repoPath);
                jGitHelper.checkout(rawIssues.get(0).getCommit_id());

                for(RawIssue rawIssue : rawIssues){
                    rawIssue.setDeveloperName(jGitHelper.getAuthorName(rawIssue.getCommit_id()));
                }
            }finally{
                if(repoPath!= null){
                    restInterfaceManager.freeRepoPath(repoId,repoPath);
                }
            }
        }


        result.put("rawIssueList",rawIssues);
        return result;
    }
}
