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

    private int judgeChangedLines = 10;

    @Autowired
    public void setLocationDao(LocationDao locationDao) {
        this.locationDao = locationDao;
    }


    @Override
    public void mapping(String repo_id, String pre_commit_id, String current_commit_id, String category, String committer) {
        int pageSize = 100;
        String repoPath = null;
        //获取该项目ignore的issue类型
        JSONArray ignoreTypes=restInterfaceManager.getIgnoreTypesOfRepo(repo_id);
        //准备要插入的tag类型
        List<JSONObject> tags = new ArrayList<>();
        //当前时间
        Date date = new Date();
        Date commitDate = getCommitDate(current_commit_id);
        String developer = getDeveloper(current_commit_id);
        //当前扫描的问题数变化
        int newIssueCount = 0;
        int remainingIssueCount = 0;
        int eliminatedIssueCount = 0;
        //要插入表的issue列表以及rawIssue列表
        List<RawIssue> insertRawIssues = new ArrayList<>();
        List<Issue> insertIssueList = new ArrayList<>();
        List<Issue> updateIssueList = new ArrayList<>();
        List<Issue> solvedIssues = new ArrayList<>();
        try{
            JSONObject repoPathJson = restInterfaceManager.getRepoPath(repo_id,current_commit_id);
            if(repoPathJson == null){
                logger.error("can not  get repo path ,repo id ----->{} , commit id -----> {}",repo_id,current_commit_id);
                throw new RuntimeException("can not get repo path");
            }
            repoPath = repoPathJson.getJSONObject("data").getString("content");


            //获取与sonar-scanner 扫描时对应的repo name
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
                        rawIssue.setStatus(RawIssueStatus.ADD.getType());
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
                List<Issue> issueList = issueDao.getSonarIssueByRepoId(repo_id, Scanner.SONAR.getType());

                String[] sonarIssueKeys = new String[issueList.size()];
                for(int i=0;i<issueList.size();i++){
                    sonarIssueKeys[i] = issueList.get(i).getSonar_issue_id();
                }
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
                        String issueUUID;
                        //二分法查找是否有相应的sonarIssueKey
                        int index = SearchUtil.dichotomy(sonarIssueKeys,sonarIssueKey);
                        //当index=-1 表示未匹配上，不为-1时表示匹配上了
                        if(index==-1){

                            Issue confirmIssue = issueDao.getIssueBySonarIssueKey(sonarIssueKey);
                            if(confirmIssue != null){
                                //判断是否是缺陷重新打开了,但是目前没有标志位表示这个缺陷是否属于缺陷重新打开，或者在某个版本已经修复了，哪个版本又重新引入
                                if("CLOSED".equals(confirmIssue.getStatus())){
                                    Issue issueReopened = issueDao.getIssueBySonarIssueKey(sonarIssueKey);
                                    if(issueReopened != null){
                                        issueReopened.setEnd_commit(current_commit_id);
                                        issueReopened.setEnd_commit_date(commitDate);
                                        issueReopened.setUpdate_time(new Date());
                                        issueReopened.setStatus(sonarIssue.getString("status"));
                                        issueReopened.setResolution(null);
                                        updateIssueList.add(issueReopened);
                                        newIssueCount++;


                                        String rawIssueUUID = UUID.randomUUID().toString();
                                        //将改issue的所有location直接表中
                                        insertLocations(rawIssueUUID,sonarIssue,repoPath);
                                        //获取rawIssue
                                        RawIssue rawIssue = getRawIssue(repo_id,current_commit_id,category,rawIssueUUID,confirmIssue.getUuid(),sonarIssue);
                                        rawIssue.setStatus(RawIssueStatus.ADD.getType());
                                        insertRawIssues.add(rawIssue);

                                        continue;
                                    }

                                }else{
                                    logger.error("sonarIssueKey  map failed --> {}  ",sonarIssueKey);
                                }

                            }

                            issueUUID = UUID.randomUUID().toString();
                            newIssueCount++;

                            String rawIssueUUID = UUID.randomUUID().toString();
                            //将改issue的所有location直接表中
                            insertLocations(rawIssueUUID,sonarIssue,repoPath);
                            //获取rawIssue
                            RawIssue rawIssue = getRawIssue(repo_id,current_commit_id,category,rawIssueUUID,issueUUID,sonarIssue);
                            rawIssue.setStatus(RawIssueStatus.ADD.getType());
                            insertRawIssues.add(rawIssue);
                            //获取issue
                            Issue issue = generateOneNewIssue(rawIssue,issueUUID,pre_commit_id,sonarIssue,date);
                            addTag(tags,ignoreTypes,rawIssue,issue);
                            insertIssueList.add(issue);
                        }else{
                            issueUUID = issueList.get(index).getUuid();
                            //将相应的下标对应数组置为1
                            sonarIssueIsMappedList[index]=1;
                            //然后分析issue的内容是否发生变化
                            //首先获取issue的最新版本rawIssue的location
                            RawIssue rawIssue = rawIssueDao.getChangedRawIssues(issueUUID).get(0);
                            //先判断flow的size是否变化。
                            JSONArray flows = sonarIssue.getJSONArray("flows");
                            JSONObject textRange = sonarIssue.getJSONObject("textRange");
                            if(textRange==null){
                                continue;
                            }
                            //判断代码是否更改过的标志
                            boolean locationsHaveChanged =false;
                            if(rawIssue.getLocations().size()==flows.size()+1){
                                //先判断issue的textRange中的行内代码是否发生过变化
                                JSONObject codeSourceMaster = restInterfaceManager.getSonarSourceLines(sonarIssue.getString("component"),textRange.getIntValue("startLine")-judgeChangedLines,textRange.getIntValue("endLine")+judgeChangedLines);
                                if(codeSourceMaster ==null){
                                   continue;
                                }
                                JSONArray sourcesMaster = codeSourceMaster.getJSONArray("sources");
                                for(int m=0;m<sourcesMaster.size();m++){
                                    JSONObject source = sourcesMaster.getJSONObject(m);
                                    boolean isNew = source.getBoolean("isNew");
                                    if(isNew){
                                        locationsHaveChanged=true;
                                        break;
                                    }
                                }
                                //再判断flows中的代码是否更新过
                                for(int n=0;n<flows.size();n++){
                                    JSONArray flowLocations = flows.getJSONObject(n).getJSONArray("locations");
                                    for(int l=0;l<flowLocations.size();l++){
                                        JSONObject flowLocation = flowLocations.getJSONObject(l);
                                        String flowComponent = flowLocation.getString("component");
                                        JSONObject flowTextRange =  flowLocation.getJSONObject("textRange");
                                        JSONObject codeSourceFlow = restInterfaceManager.getSonarSourceLines(flowComponent,flowTextRange.getIntValue("startLine"),flowTextRange.getIntValue("endLine"));
                                        if(codeSourceMaster ==null){
                                            continue;
                                        }
                                        JSONArray sourcesFlow = codeSourceFlow.getJSONArray("sources");
                                        for(int k=0;k<sourcesFlow.size();k++){
                                            JSONObject source = sourcesFlow.getJSONObject(k);
                                            Boolean isNew = source.getBoolean("isNew");
                                            if(Boolean.TRUE.equals(isNew)){
                                                locationsHaveChanged=true;
                                                break;
                                            }
                                        }
                                    }
                                    if(locationsHaveChanged){
                                        break;
                                    }
                                }
                            }

                            String rawIssueUUID = UUID.randomUUID().toString();
                            //获取rawIssue
                            RawIssue newRawIssue = getRawIssue(repo_id,current_commit_id,category,rawIssueUUID,issueUUID,sonarIssue);
                            if(locationsHaveChanged){
                                //如果有改变则记录更改后的location以及rawIssue
                                insertLocations(rawIssueUUID,sonarIssue,repoPath);
                                newRawIssue.setStatus(RawIssueStatus.CHANGED.getType());
                            }else{

                            }
                            insertRawIssues.add(newRawIssue);

                            //更新issue的状态
                            Issue updateIssue = issueList.get(index);
                            updateIssue.setEnd_commit(current_commit_id);
                            updateIssue.setEnd_commit_date(commitDate);
                            updateIssue.setUpdate_time(new Date());
                            updateIssue.setStatus(sonarIssue.getString("status"));
                            updateIssue.setResolution(null);
                            updateIssueList.add(updateIssue);
                        }
                    }
                }
                for(int m=0;m<sonarIssueIsMappedList.length;m++){
                    if(sonarIssueIsMappedList[m]==0){
                        solvedIssues.add(issueList.get(m));
                    }
                }
                //实际解决的缺陷总数
                int realResolvedIssueCounts=0;
                //solved issue 数量不为零
                if(!solvedIssues.isEmpty()){
                    //数据库中未匹配的则是solved issue的总数量
                    eliminatedIssueCount = solvedIssues.size();
                    Issue[] solvedIssuesArray = solvedIssues.toArray(new Issue[solvedIssues.size()]);
                    int l =0;
                    //此处效率很差，后面需要重构成遍历for循环

                    List<JSONObject> sonarSolvedIssuesAll = new ArrayList<>();

                    while(l<solvedIssues.size()) {
                        //因为path url的长度有限制，所以每次只拼接5个sonar issue key发送请求
                        StringBuilder solvedSonarIssueKeysStringBuilder = new StringBuilder();
                        List<String> issueIds =new ArrayList<>();
                        if (l > solvedIssues.size() - 5) {
                            for (; l < solvedIssues.size(); l++) {
                                solvedSonarIssueKeysStringBuilder.append(solvedIssuesArray[l].getSonar_issue_id() + ",");
                                issueIds.add(solvedIssuesArray[l].getUuid());
                            }
                        } else {
                            int times = l + 5;
                            for (; l < times; l++) {
                                solvedSonarIssueKeysStringBuilder.append(solvedIssuesArray[l].getSonar_issue_id() + ",");
                                issueIds.add(solvedIssuesArray[l].getUuid());
                            }
                        }
                        String solvedSonarIssueKeys = solvedSonarIssueKeysStringBuilder.toString().substring(0, solvedSonarIssueKeysStringBuilder.toString().length() - 1);


                        //获取sonar相应issue key的 issue信息
                        JSONObject sonarSolvedIssues = restInterfaceManager.getSonarIssueResultsBySonarIssueKey(solvedSonarIssueKeys, 0);
                        realResolvedIssueCounts += sonarSolvedIssues.getIntValue("total");

                        if (sonarSolvedIssues.getIntValue("total") != solvedSonarIssueKeys.split(",").length) {
                            int total = sonarSolvedIssues.getIntValue("total");
                            logger.error("sonar issue keys --> {}  ,db result is not equal to sonar db ,sonar total is {}", solvedSonarIssueKeys,total);
                        }

                        List<Issue> correspondingIssues = issueDao.getIssuesByIssueIds(issueIds);
                        JSONArray solvedSonarIssues = sonarSolvedIssues.getJSONArray("issues");
                        for(Issue solvedIssue : correspondingIssues){
                            for (int k = 0; k < solvedSonarIssues.size(); k++) {
                                JSONObject solvedSonarIssue = solvedSonarIssues.getJSONObject(k);
                                if(solvedIssue.getSonar_issue_id().equals(solvedSonarIssue.getString("key"))){
                                    solvedIssue.setStatus(solvedSonarIssue.getString("status"));
                                    solvedIssue.setResolution(solvedSonarIssue.getString("resolution"));
                                    String rawIssueUUID = UUID.randomUUID().toString();
                                    //获取rawIssue
                                    RawIssue newRawIssue = getRawIssue(repo_id,current_commit_id,category,rawIssueUUID,solvedIssue.getUuid(),solvedSonarIssue);
                                    newRawIssue.setStatus(RawIssueStatus.SOLVED.getType());
                                    insertRawIssues.add(newRawIssue);
                                    break;
                                }
                            }
                            if(!"CLOSED".equals(solvedIssue.getStatus()) || solvedIssue.getResolution()==null || solvedIssue.getResolution().isEmpty()){
                                solvedIssue.setStatus("NON-MATCHED");
                            }
                            solvedIssue.setUpdate_time(new Date());

                            updateIssueList.add(solvedIssue);
                        }

                    }






//                        for(Issue solvedIssue:
//                                solvedIssues){
//                            for(int k=0;k<solvedSonarIssues.size();k++){
//                                JSONObject solvedSonarIssue = solvedSonarIssues.getJSONObject(k);
//                                if(solvedIssue.getSonar_issue_id().equals(solvedSonarIssue.getString("key"))){
////                                    String rawIssueUUID = UUID.randomUUID().toString();
////                                    String issueUUID = solvedIssue.getUuid();
////                                    //将改issue的所有location直接表中
////                                    insertLocations(rawIssueUUID,solvedSonarIssue,repoPath);
////                                    //获取rawIssue
////                                    RawIssue rawIssue = getRawIssue(repo_id,current_commit_id,category,rawIssueUUID,issueUUID,solvedSonarIssue);
////                                    insertRawIssues.add(rawIssue);
//                                    //此处后面更改为请求bugRecommendation接口
//                                    //更新issue状态
//
//                                    solvedIssue.setStatus(solvedSonarIssue.getString("status"));
//                                    solvedIssue.setResolution(solvedSonarIssue.getString("resolution"));
//                                    break;
//                                }
//
//                            }
//
//                        }
//
//                    }
//                    for(Issue solvedIssue:
//                            solvedIssues){
//                        //如果没有匹配上则将status 置为 NON-MATCHED
//                        if(!"CLOSED".equals(solvedIssue.getStatus()) || solvedIssue.getResolution()==null || solvedIssue.getResolution().isEmpty()){
//                            solvedIssue.setStatus("NON-MATCHED");
//                        }
//                        //暂时先不加入修改priority，考虑到sonar跟本地修改priority的冲突
//                        //目前是以缺陷出现的最后一个commit作为end_commit
////                        solvedIssue.setEnd_commit(current_commit_id);
////                        solvedIssue.setEnd_commit_date(commitDate);
//                        solvedIssue.setUpdate_time(new Date());
//
//                        updateIssueList.add(solvedIssue);
//                    }



                }
                if(realResolvedIssueCounts!=eliminatedIssueCount){
                    logger.error("commit_id --> {},the count of solved issues is not correct,cause sonar find ->{}, db find -->{} ",current_commit_id,realResolvedIssueCounts,eliminatedIssueCount);
                }

                //remain Issue
                remainingIssueCount = issueTotal;
                log.info("finish mapping -> new:{},remaining:{},eliminated:{} . category --> {}",newIssueCount,remainingIssueCount,eliminatedIssueCount,category);
                dashboardUpdate(repo_id, newIssueCount, remainingIssueCount, eliminatedIssueCount,category);
                scanResultDao.addOneScanResult(new ScanResult(category,repo_id,date,current_commit_id,commitDate,developer,newIssueCount,eliminatedIssueCount,remainingIssueCount));
            }

            //更新issue
            if (!updateIssueList.isEmpty()) {
                //更新issue
                issueDao.batchUpdateSonarIssues(updateIssueList);
                logger.info("issue update success!");
            }

            if (!insertRawIssues.isEmpty()) {
                rawIssueDao.insertRawIssueList(insertRawIssues);
                logger.info("new raw issue insert success!");
            }

            if (!insertIssueList.isEmpty()) {
                issueDao.insertIssueList(insertIssueList);
                issueEventManager.sendIssueEvent(EventType.NEW_BUG,insertIssueList,committer,repo_id,commitDate);
                newIssueInfoUpdate(insertIssueList,category,repo_id);
                logger.info("new issue insert success!");
            }

            //打tag
            if(!tags.isEmpty()){
                restInterfaceManager.addTags(tags);
            }
            logger.info("mapping finished!");

        }catch(Exception e){
            e.printStackTrace();
            logger.error("current_commit_id --> {} ,repo_id --> {} mapping failed" ,current_commit_id,repo_id);
        }finally {
            if(repoPath!= null){
                restInterfaceManager.freeRepoPath(repo_id,repoPath);
            }
        }

    }



    public void insertLocations(String rawIssueUUID,JSONObject issue,String repoPath) throws Exception{
        int startLine =0;
        int endLine = 0;
        String sonar_path;
        String[] sonarComponents;
        String filePath = null;
        List<Location> locations = new ArrayList<>();
        //分成两部分处理，第一部分针对issue中的textRange存储location
        JSONObject textRange = issue.getJSONObject("textRange");
        if(textRange != null){
            startLine = textRange.getIntValue("startLine");
            endLine = textRange.getIntValue("endLine");
        }else{
            logger.error("textRange is null , sonar issue-->",issue.toJSONString());
        }

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

    private Location getLocation(int startLine,int endLine,String rawIssueId,String filePath,String repoPath) throws Exception{
        Location location = new Location();
        String locationUUID = UUID.randomUUID().toString();
        //获取相应的code
        String code= null;
        try{
            code = ASTUtil.getCode(startLine,endLine,repoPath+"/"+filePath);
        }catch (Exception e){
            logger.info("file path --> {} file deleted",repoPath+"/"+filePath);
            logger.error("rawIssueId --> {}  get code failed.",rawIssueId);

        }

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
//        JSONObject scan = restInterfaceManager.getScanByCategoryAndRepoIdAndCommitId(repo_id,current_commit_id,category);
//        if(scan == null){
//            logger.error("can not get scan, with repo_id-->{} ,commit_id --> {} ,category --> {}" ,repo_id,current_commit_id,category);
//            return null;
//        }

        //根据ruleId获取rule的name
        String issueName=null;
        JSONObject rule = restInterfaceManager.getRuleInfo(issue.getString("rule"),null,null);
        if(rule != null){
            issueName = rule.getJSONObject("rule").getString("name");
        }
        //获取文件路径
        String[] sonarComponents;
        String sonarPath =issue.getString("component");
        String filePath= null;
        if(sonarPath != null) {
            sonarComponents = sonarPath.split(":");
            if (sonarComponents.length >= 2) {
                filePath = sonarComponents[sonarComponents.length - 1];
            }
        }

        RawIssue rawIssue = new RawIssue();
        rawIssue.setCategory(category);
        rawIssue.setUuid(rawIssueUUID);
        rawIssue.setType(issueName);
        rawIssue.setFile_name(filePath);
        rawIssue.setDetail(issue.getString("message"));
        //待改，因为数据库不可为空
        rawIssue.setScan_id("sonar");
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
        int priority=-1;
        String severity = sonarIssue.getString("severity");
        if(severity!=null){
            priority =translateSeverityToPriority(sonarIssue.getString("severity"));
        }else{
            logger.error("severity --> is null ,rawIssue  id--> {}" ,rawIssue.getUuid());
        }

        issue.setPriority(priority);
        issue.setSonar_issue_id(sonarIssue.getString("key"));
        return issue;
    }

    private int translateSeverityToPriority(String severity){
        int priority=-1;
        if(severity!=null){
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
        }else{
            logger.error("severity --> is null ");
        }
        return priority;
    }
}
