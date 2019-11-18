package cn.edu.fudan.issueservice.service.impl;

import cn.edu.fudan.issueservice.dao.LocationDao;
import cn.edu.fudan.issueservice.dao.RawIssueDao;
import cn.edu.fudan.issueservice.domain.*;
import cn.edu.fudan.issueservice.util.ASTUtil;
import cn.edu.fudan.issueservice.util.SearchUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
@Slf4j
@Service("sonarMapping")
public class SonarMappingServiceImpl extends BaseMappingServiceImpl{

    private static Logger logger = LoggerFactory.getLogger(SonarMappingServiceImpl.class);

    private LocationDao locationDao;

    @Autowired
    public void setLocationDao(LocationDao locationDao) {
        this.locationDao = locationDao;
    }


    @Override
    public void mapping(String repo_id, String pre_commit_id, String current_commit_id, String category, String committer) {
        int pageSize = 100;
        String repoPath = null;
        JSONArray ignoreTypes=restInterfaceManager.getIgnoreTypesOfRepo(repo_id);//获取该项目ignore的issue类型
        //准备要插入的tag类型
        List<JSONObject> tags = new ArrayList<>();
        //当前时间
        Date date = new Date();
        Date commitDate=getCommitDate(current_commit_id);
        String developer=getDeveloper(current_commit_id);
        //当前扫描的问题数变化
        int newIssueCount = 0;
        int remainingIssueCount = 0;
        int eliminatedIssueCount = 0;
        //要插入表的issue列表以及rawIssue列表
        List<RawIssue> insertRawIssues = new ArrayList<>();
        List<Issue> insertIssueList = new ArrayList<>();
        try{
            JSONObject repoPathJson = restInterfaceManager.getRepoPath(repo_id,current_commit_id);
            if(repoPathJson == null){
                logger.error("can not  get repo path ,repo id ----->{} , commit id -----> {}",repo_id,current_commit_id);
                throw new RuntimeException("can not get repo path");
            }
            repoPath = repoPathJson.getJSONObject("data").getString("content");


            //获取与sonnar-scanner 扫描时对应的repo name
            JSONObject currentRepo = restInterfaceManager.getRepoById(repo_id);
            String localAddress=currentRepo.getJSONObject("data").getString("local_addr");
            String repoName = localAddress.substring(localAddress.lastIndexOf("/")+1);
            //请求sonar接口获取信息
            JSONObject sonarIssueResult = restInterfaceManager.getSonarIssueResults(repoName,null,1,false,0);
            int issueTotal = sonarIssueResult.getIntValue("total");
            //考虑到可能issue数量太大，分页获取
            int pages = issueTotal%pageSize>0?issueTotal/pageSize+1:issueTotal/pageSize;
            //判断是否是第一次入库
            if (pre_commit_id.equals(current_commit_id)) {
                for(int i=1;i<=pages;i++){
                    //获取第i页的全部issue结果
                    JSONObject sonarResult = restInterfaceManager.getSonarIssueResults(repoName,null,pageSize,false,i);
                    JSONArray issues = sonarResult.getJSONArray("issues");
                    //遍历存储 location,rawIssue,issue
                    for(int j=0; j<issues.size();j++){
                        String rawIssueUUID = UUID.randomUUID().toString();
                        String issueUUID = UUID.randomUUID().toString();
                        JSONObject sonarIssue = issues.getJSONObject(j);
                        //将改issue的所有location直接表中
                        insertLocations(rawIssueUUID,sonarIssue,repoPath);
                        //获取rawIssue
                        RawIssue rawIssue = getRawIssue(repo_id,current_commit_id,category,rawIssueUUID,issueUUID,sonarIssue);
                        insertRawIssues.add(rawIssue);
                        //获取issue
                        Issue issue = generateOneNewIssue(rawIssue,issueUUID,pre_commit_id,sonarIssue,date);
                        addTag(tags,ignoreTypes,rawIssue,issue);
                        insertIssueList.add(issue);
                    }
                }
                //更新dashboard
                newIssueCount = insertIssueList.size();
                remainingIssueCount = insertIssueList.size();
                log.info("finish mapping -> new:{},remaining:{},eliminated:{} . category --> {}",newIssueCount,remainingIssueCount,eliminatedIssueCount,category);
                dashboardUpdate(repo_id, newIssueCount, remainingIssueCount, eliminatedIssueCount,category);
                scanResultDao.addOneScanResult(new ScanResult(category,repo_id,date,current_commit_id,commitDate,developer,newIssueCount,eliminatedIssueCount,remainingIssueCount));
            }else{
                //先获取数据库所有的sonarIssueKey，并且已经排序，用作后面的二分法查找。
                List<Issue> issueList = issueDao.getSonarIssueKeysByRepoId(repo_id, Scanner.SONAR.getType());
                List<String> sonarIssueKeysList = new ArrayList<>();
                for(Issue issue : issueList){
                    sonarIssueKeysList.add(issue.getSonar_issue_id());
                }
                String[] sonarIssueKeys = sonarIssueKeysList.toArray(new String[sonarIssueKeysList.size()]);
                int[] sonarIssueIsMappedList = new int[sonarIssueKeys.length];
                //分页获取sonarIssue结果
                for(int i=1;i<=pages;i++){
                    //获取第i页的全部issue结果
                    JSONObject sonarResult = restInterfaceManager.getSonarIssueResults(repoName,null,pageSize,false,i);
                    JSONArray issues = sonarResult.getJSONArray("issues");
                    //遍历存储 location,rawIssue,issue
                    for(int j=0; j<issues.size();j++){
                        JSONObject sonarIssue = issues.getJSONObject(j);
                        String sonarIssueKey = sonarIssue.getString("key");
                        String issueUUID = UUID.randomUUID().toString();
                        //二分法查找是否有相应的sonarIssueKey
                        int index = SearchUtil.dichotomy(sonarIssueKeys,sonarIssueKey);
                        //当index=-1 表示未匹配上，不为-1时表示匹配上了
                        if(index==-1){
                            newIssueCount++;
                            String rawIssueUUID = UUID.randomUUID().toString();
                            //将改issue的所有location直接表中
                            insertLocations(rawIssueUUID,sonarIssue,repoPath);
                            //获取rawIssue
                            RawIssue rawIssue = getRawIssue(repo_id,current_commit_id,category,rawIssueUUID,issueUUID,sonarIssue);
                            insertRawIssues.add(rawIssue);
                            //获取issue
                            Issue issue = generateOneNewIssue(rawIssue,issueUUID,pre_commit_id,sonarIssue,date);
                            addTag(tags,ignoreTypes,rawIssue,issue);
                            insertIssueList.add(issue);
                        }else{
                            //将相应的下标对应数组置为1
                            sonarIssueIsMappedList[index]=1;
                            //然后分析issue的内容是否发生变化
                            //首先获取issue的最新版本rawIssue的location
                            //rawIssueDao.getRawIssueByIssueId();
                        }
                    }
                }
            }
        }catch(Exception e){

        }finally {
            if(repoPath!= null){
                restInterfaceManager.freeRepoPath(repo_id,repoPath);
            }
        }

    }



    public void insertLocations(String rawIssueUUID,JSONObject issue,String repoPath){
        int startLine;
        int endLine;
        String sonar_path;
        String[] sonarComponents;
        String filePath = null;
        List<Location> locations = new ArrayList<>();
        //分成两部分处理，第一部分针对issue中的textRange存储location
        JSONObject textRange = issue.getJSONObject("textRange");
        startLine = textRange.getIntValue("startLine");
        endLine = textRange.getIntValue("endLine");
        sonar_path =issue.getString("component");
        if(sonar_path != null) {
            sonarComponents = sonar_path.split(":");
            if (sonarComponents.length >= 2) {
                filePath = sonarComponents[sonarComponents.length - 1];
            }
        }
        Location mainLocation = getLocation(startLine,endLine,rawIssueUUID,filePath,repoPath);
        locations.add(mainLocation);
        //第二部分针对issue中的flows中的所有location存储
        JSONArray flows = issue.getJSONArray("flows");
        for(int i=0;i<flows.size();i++){
            JSONObject flow = flows.getJSONObject(i);
            JSONArray flowLocations = flow.getJSONArray("locations");
            //一个flows里面有多个locations， locations是一个数组，目前看sonar的结果每个locations都是一个location，但是不排除有多个。
            for(int j=0;j<flowLocations.size();j++){
                JSONObject flowLocation = flowLocations.getJSONObject(j);
                String flowComponent = flowLocation.getString("component");
                JSONObject flowTextRange = flowLocation.getJSONObject("textRange");
                if(flowTextRange==null || flowComponent == null){
                    continue;
                }
                int flowStartLine = flowTextRange.getIntValue("startLine");
                int flowEndLine = flowTextRange.getIntValue("endLine");
                String flowFilePath = null;

                String[] flowComponents = flowComponent.split(":");
                if (flowComponents.length >= 2) {
                    flowFilePath = flowComponents[flowComponents.length - 1];
                }

                Location location = getLocation(flowStartLine,flowEndLine,rawIssueUUID,flowFilePath,repoPath);
                locations.add(location);
            }
        }
        //最后加所有加入location链表的数据插入数据库
        locationDao.insertLocationList(locations);


    }

    private Location getLocation(int startLine,int endLine,String rawIssueId,String filePath,String repoPath){
        Location location = new Location();
        String locationUUID = UUID.randomUUID().toString();
        //获取相应的code
        String code = ASTUtil.getCode(startLine,endLine,repoPath+"/"+filePath);
        location.setCode(code);

        location.setUuid(locationUUID);
        location.setStart_line(startLine);
        location.setEnd_line(endLine);
        StringBuilder lines = new StringBuilder();
        if(startLine>endLine){
            logger.error("startLine number greater than endLine number");
            return null;
        }else if(startLine==endLine){
            location.setBug_lines(startLine+"");
        }else{
            while(startLine<endLine){
                lines.append(startLine+",");
                startLine++;
            }
            lines.append(endLine);
        }
        location.setBug_lines(lines.toString());
        location.setFile_path(filePath);
        location.setRawIssue_id(rawIssueId);
        return location;
    }

    private RawIssue getRawIssue(String repo_id, String current_commit_id, String category,String rawIssueUUID,String issueUUID,JSONObject issue){
        //根据category,repoId,commitId,获取唯一的scanId
        JSONObject scan = restInterfaceManager.getScanByCategoryAndRepoIdAndCommitId(repo_id,current_commit_id,category);
        if(scan == null){
            logger.error("can not get scan, with repo_id-->{} ,commit_id --> {} ,category --> {}" ,repo_id,current_commit_id,category);
            return null;
        }

        //根据ruleId获取rule的name
        String issueName=null;
        JSONObject rule = restInterfaceManager.getRuleInfo(issue.getString("rule"),null,null);
        if(rule != null){
            issueName = rule.getJSONObject("rule").getString("name");
        }
        //获取文件路径
        String[] sonarComponents;
        String sonar_path =issue.getString("component");
        String filePath= null;
        if(sonar_path != null) {
            sonarComponents = sonar_path.split(":");
            if (sonarComponents.length >= 2) {
                filePath = sonarComponents[sonarComponents.length - 1];
            }
        }

        RawIssue rawIssue = new RawIssue();
        rawIssue.setCategory(category);
        rawIssue.setUuid(rawIssueUUID);
        rawIssue.setType(issueName);
        rawIssue.setFile_name(filePath);
        rawIssue.setScan_id(scan.getString("uuid"));
        rawIssue.setIssue_id(issueUUID);
        rawIssue.setCommit_id(current_commit_id);
        rawIssue.setRepo_id(repo_id);
        return rawIssue;
    }


    public Issue generateOneNewIssue(RawIssue rawIssue,String issueUUID,String startCommit,JSONObject sonarIssue,Date date){

        Issue issue = new Issue();
        issue.setUuid(issueUUID);
        issue.setType(rawIssue.getType());
        issue.setCategory(rawIssue.getCategory());
        issue.setStart_commit(startCommit);
        JSONObject startCommitTimeJson = restInterfaceManager.getCommitTime(startCommit);
        if(startCommitTimeJson!=null && startCommitTimeJson.getJSONObject("data") != null){
            issue.setStart_commit_date(startCommitTimeJson.getJSONObject("data").getDate("commit_time"));
        }
        issue.setEnd_commit(rawIssue.getCommit_id());
        JSONObject endCommitTimeJson = restInterfaceManager.getCommitTime(rawIssue.getCommit_id());
        if(endCommitTimeJson!=null && endCommitTimeJson.getJSONObject("data") != null){
            issue.setEnd_commit_date(endCommitTimeJson.getJSONObject("data").getDate("commit_time"));
        }
        issue.setRepo_id(rawIssue.getRepo_id());
        issue.setCreate_time(date);
        issue.setUpdate_time(date);
        boolean hasDisplayId=issueDao.getMaxIssueDisplayId(rawIssue.getRepo_id()) != null;
        if (isDefaultDisplayId){
            currentDisplayId = hasDisplayId ? issueDao.getMaxIssueDisplayId(rawIssue.getRepo_id()) : 0;
            isDefaultDisplayId = false;
        }
        String status = sonarIssue.getString("status");
        issue.setStatus(status);
        if("RESOLVED".equals(status)){
            issue.setResolution(sonarIssue.getString("resolution"));
        }
        issue.setDisplayId(++currentDisplayId);

        String severity = sonarIssue.getString("severity");
        int priority ;
        switch (severity){
            case "BLOCKER":
                priority = 0;
                break;
            case "CRITICAL":
                priority = 1;
                break;
            case "MAJOR":
                priority = 2;
                break;
            case "MINOR":
                priority = 3;
                break;
            case "INFO":
                priority = 4;
                break;
            default:
                logger.error("severity --> {} this case have not taken into account !",severity);
                priority = -1;
        }
        issue.setPriority(priority);
        issue.setSonar_issue_id(sonarIssue.getString("key"));
        return issue;
    }
}
