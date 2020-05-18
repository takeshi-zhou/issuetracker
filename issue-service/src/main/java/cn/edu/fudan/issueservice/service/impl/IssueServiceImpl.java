package cn.edu.fudan.issueservice.service.impl;

import cn.edu.fudan.issueservice.component.RestInterfaceManager;
import cn.edu.fudan.issueservice.dao.IssueDao;
import cn.edu.fudan.issueservice.dao.ScanResultDao;
import cn.edu.fudan.issueservice.domain.*;
import cn.edu.fudan.issueservice.scheduler.QuartzScheduler;
import cn.edu.fudan.issueservice.service.IssueService;
import cn.edu.fudan.issueservice.util.DateTimeUtil;
import cn.edu.fudan.issueservice.util.IssueUtil;
import cn.edu.fudan.issueservice.util.JGitHelper;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author WZY
 * @version 1.0
 **/
@Slf4j
@Service
public class IssueServiceImpl implements IssueService {
    private Logger logger = LoggerFactory.getLogger(IssueServiceImpl.class);

    @Value("${solved.tag_id}")
    private String solvedTagId;
    @Value("${ignore.tag_id}")
    private String ignoreTagId;

    private RestInterfaceManager restInterfaceManager;
    private IssueUtil issueUtil;

    @Autowired
    public void setIssueUtil(IssueUtil issueUtil) {
        this.issueUtil = issueUtil;
    }

    @Autowired
    public void setRestInterfaceManager(RestInterfaceManager restInterfaceManager) {
        this.restInterfaceManager = restInterfaceManager;
    }

    private CloneMappingServiceImpl cloneMappingService;

    @Autowired
    public void setCloneMappingService(CloneMappingServiceImpl cloneMappingService) {
        this.cloneMappingService = cloneMappingService;
    }

    private BugMappingServiceImpl bugMappingService;

    @Autowired
    public void setBugMappingService(BugMappingServiceImpl bugMappingService) {
        this.bugMappingService = bugMappingService;
    }

    private SonarMappingServiceImpl sonarMappingService;

    @Autowired
    public void setSonarMappingService(SonarMappingServiceImpl sonarMappingService) {
        this.sonarMappingService = sonarMappingService;
    }

    private QuartzScheduler quartzScheduler;

    @Autowired
    public void setQuartzScheduler(QuartzScheduler quartzScheduler) {
        this.quartzScheduler = quartzScheduler;
    }

    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    private IssueDao issueDao;

    @Autowired
    public void setIssueDao(IssueDao issueDao) {
        this.issueDao = issueDao;
    }

    private ScanResultDao scanResultDao;

    @Autowired
    public void setScanResultDao(ScanResultDao scanResultDao) {
        this.scanResultDao = scanResultDao;
    }


    @Override
    public void insertIssueList(List<Issue> list) {
        issueDao.insertIssueList(list);
    }

    @Override
    public void deleteIssueByRepoIdAndCategory(String repoId,String category) {
        logger.info("start to delete issue -> repoId={} , category={}",repoId,category);

        //先删除该项目所有issue对应的tag
        List<String> issueIds = issueDao.getIssueIdsByRepoIdAndCategory(repoId, category);
        restInterfaceManager.deleteTagsOfIssueInOneRepo(issueIds);
        logger.info("tag delete success!");
        issueDao.deleteIssueByRepoIdAndCategory(repoId,category);
        logger.info("issue delete success!");
        scanResultDao.deleteScanResultsByRepoIdAndCategory(repoId, category);
        logger.info("scan result delete success!");

    }

    @Override
    public void deleteScanResultsByRepoIdAndCategory(String repo_id, String category) {
        scanResultDao.deleteScanResultsByRepoIdAndCategory(repo_id, category);
    }

    @Override
    public void batchUpdateIssue(List<Issue> list) {
        issueDao.batchUpdateIssue(list);
    }

    @Override
    public Issue getIssueByID(String uuid) {
        return issueDao.getIssueByID(uuid);
    }

    private void addTagInfo(List<Issue> issues) {
        for (Issue issue : issues) {
            JSONArray tags = restInterfaceManager.getTagsOfIssue(issue.getUuid());
            issue.setTags(tags);
        }
    }

    @Override
    public Object getIssueList(String project_id, Integer page, Integer size,String category) {
        String repoId=restInterfaceManager.getRepoIdOfProject(project_id);
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("repo_id", repoId);
        param.put("size", size);
        param.put("category",category);

        List<String> status = new ArrayList<>();
        status.add(StatusEnum.OPEN.getName());
        status.add(StatusEnum.TO_REVIEW.getName());
        param.put("issue_status", status);

        int count = issueDao.getIssueCount(param);
        param.put("start", (page - 1) * size);
        result.put("totalPage", count % size == 0 ? count / size : count / size + 1);
        result.put("totalCount", count);
        List<Issue> issues = issueDao.getIssueList(param);
        addTagInfo(issues);
        result.put("issueList", issues);
        return result;
    }

    @Override
    public Object getFilteredIssueList(JSONObject requestParam) {
        String project_id = requestParam.getString("project_id");
        int size = requestParam.getIntValue("size");
        int page = requestParam.getIntValue("page");
        String category=requestParam.getString("category");
        if (project_id == null || size == 0 || page == 0) {
            throw new IllegalArgumentException("param lost!");
        }
        String repoId = restInterfaceManager.getRepoIdOfProject(project_id);
        JSONArray tag_ids = requestParam.getJSONArray("tags");
        JSONArray types = requestParam.getJSONArray("types");
        String developerName = requestParam.getString("developer");
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> query = new HashMap<>();
        if(tag_ids!=null&&!tag_ids.isEmpty()){
            //只有在筛选条件都不为null的条件下才启用过滤
            JSONObject requestTagPrams = new JSONObject();
            requestTagPrams.put("repo_id",repoId);
            requestTagPrams.put("tag_ids",tag_ids.toJavaList(String.class));
            JSONArray issue_ids = restInterfaceManager.getSpecificTaggedIssueIdsByTagIdsAndRepoId(requestTagPrams);
            if (issue_ids == null || issue_ids.size() == 0) {//没找到打了这些tag的issue
                result.put("totalPage", 0);
                result.put("totalCount", 0);
                result.put("issueList", Collections.emptyList());
                return result;
            }
            query.put("issue_ids", issue_ids.toJavaList(String.class));
        }
        if(types!=null&&!types.isEmpty()){
            query.put("types", types.toJavaList(String.class));
        }
        query.put("repo_id", repoId);
        query.put("category",category);
        query.put("developer",developerName);
        int count = issueDao.getIssueCount(query);
        query.put("size", size);
        query.put("start", (page - 1) * size);
        List<Issue> issues = issueDao.getIssueList(query);
        addTagInfo(issues);

        result.put("totalPage", count % size == 0 ? count / size : count / size + 1);
        result.put("totalCount", count);
        result.put("issueList", issues);
        return result;
    }

    private final static Map<String,Object> empty=new HashMap<>();

    static {
        empty.put("totalPage", 0);
        empty.put("totalCount", 0);
        empty.put("issueList", Collections.emptyList());
    }

    @Override
    public Object getSpecificIssues(IssueParam issueParam,String userToken) {
        Map<String, Object> result = new HashMap<>();

        //必须的几个参数
        String duration=issueParam.getDuration();
        int size=issueParam.getSize();
        int page=issueParam.getPage();
        String category=issueParam.getCategory();
        boolean onlyNew=issueParam.isOnlyNew();
        boolean onlyEliminated=issueParam.isOnlyEliminated();

        //可有可无的几个参数
        String projectId=issueParam.getProjectId();
        List<String> types=issueParam.getTypes();
        List<String> tags=issueParam.getTags();


        Map<String, Object> query = new HashMap<>();
        List<String> issueIds=new ArrayList<>();
        if(onlyNew||onlyEliminated){
            //是通过dashboard点击查询的
            if(projectId!=null&&!projectId.equals("")){
                //查询单个项目
                String repoId = restInterfaceManager.getRepoIdOfProject(projectId);
                query.put("repo_id",repoId);
                addSpecificIssueIdsForRepo(onlyNew,onlyEliminated,repoId,duration,category,issueIds);
            }else{
                //查询该用户的所有项目
                String account_id = restInterfaceManager.getAccountId(userToken);
                JSONArray repoIds = restInterfaceManager.getRepoIdsOfAccount(account_id,category);
                if (repoIds != null&&!repoIds.isEmpty()) {
                    for (int i = 0; i < repoIds.size(); i++) {
                        String currentRepoId = repoIds.getString(i);
                        addSpecificIssueIdsForRepo(onlyNew,onlyEliminated,currentRepoId,duration,category,issueIds);
                    }
                }else{
                    //当前用户没有项目
                    return empty;
                }
            }
            //如果从dashboard过来只查新增和干掉的，但redis并没有相应的issue id,返回empty
            if(issueIds.isEmpty()) {
                return empty;
            }
        }else{
            //点击项目列表查询的，必定是单个项目
            String repoId = restInterfaceManager.getRepoIdOfProject(projectId);
            query.put("repo_id",repoId);
        }
        //根据tag来筛选
        if(tags!=null&&!tags.isEmpty()){
            //特定tag的issue ids
            JSONArray specificTaggedIssueIds = restInterfaceManager.getSpecificTaggedIssueIds(tags);
            if(specificTaggedIssueIds==null||specificTaggedIssueIds.isEmpty()){
                return empty;
            }
            List<String> issueIdsAfterFilter=new ArrayList<>();
            for(String issueId:issueIds){
                //筛选掉那些不是特定tag的issue id
                if(specificTaggedIssueIds.contains(issueId)){
                    issueIdsAfterFilter.add(issueId);
                }
            }
            if(!issueIdsAfterFilter.isEmpty()) {
                query.put("list",issueIdsAfterFilter);
            } else{
                if(!specificTaggedIssueIds.isEmpty()) {
                    query.put("list",specificTaggedIssueIds.toJavaList(String.class));
                }
            }
        }else{
            //不根据tag来筛选时需要自动过滤solved的和ignore的issue_ids
            if(!issueIds.isEmpty()) {
                query.put("list",issueIds);
            }

            List<String> tag_ids = new ArrayList<>();
            tag_ids.add(solvedTagId);
            tag_ids.add(ignoreTagId);
            JSONArray solved_issue_ids = restInterfaceManager.getSolvedIssueIds(tag_ids);
            if (solved_issue_ids != null && solved_issue_ids.size() > 0) {
                query.put("solved_issue_ids", solved_issue_ids.toJavaList(String.class));
            }
        }
        //根据类型来筛选
        if(types!=null&&!types.isEmpty()){
            query.put("types",types);
        }

        query.put("category",issueParam.getCategory());
        int count=issueDao.getSpecificIssueCount(query);
        query.put("size", size);
        query.put("start", (page - 1) * size);
        List<Issue> issues = issueDao.getSpecificIssues(query);
        addTagInfo(issues);
        result.put("totalPage", count % size == 0 ? count / size : count / size + 1);
        result.put("totalCount", count);
        result.put("issueList", issues);
        return result;
    }

    private void addSpecificIssueIdsForRepo(boolean onlyNew,boolean onlyEliminated,String repoId,String duration,String category,List<String> issueIds){
        if(onlyNew){
            List<String> newIssueIds=stringRedisTemplate.opsForList().range("dashboard:"+category+":"+duration+":new:"+ repoId,0,-1);
            if(newIssueIds!=null&&!newIssueIds.isEmpty()) {
                issueIds.addAll(newIssueIds);
            }
        }else if(onlyEliminated){
            List<String> eliminatedIssueIds=stringRedisTemplate.opsForList().range("dashboard:"+category+":"+duration+":eliminated:"+ repoId,0,-1);
            if(eliminatedIssueIds!=null&&!eliminatedIssueIds.isEmpty()) {
                issueIds.addAll(eliminatedIssueIds);
            }
        }
    }


    private IssueCount getOneRepoDashBoardInfo(String duration,String repoId,String category){
        Object newObject=stringRedisTemplate.opsForHash().get("dashboard:"+category+":"+ duration + ":" + repoId, "new");
        int newCount = newObject==null?0:Integer.parseInt((String)newObject);
        Object remainingObject=stringRedisTemplate.opsForHash().get("dashboard:"+category+":" + duration + ":" + repoId, "remaining");
        int remainingCount = remainingObject==null?0:Integer.parseInt((String)remainingObject);
        Object eliminatedObject=stringRedisTemplate.opsForHash().get("dashboard:"+category+":" + duration + ":" + repoId, "eliminated");
        int eliminatedCount = eliminatedObject==null?0:Integer.parseInt((String)eliminatedObject);
        List<String> newIssueIds=stringRedisTemplate.opsForList().range("dashboard:"+category+":"+duration+":new:"+ repoId,0,-1);
        List<String> eliminatedIssueIds=stringRedisTemplate.opsForList().range("dashboard:"+category+":"+duration+":eliminated:"+ repoId,0,-1);
        return new IssueCount(newCount,eliminatedCount,remainingCount,newIssueIds,eliminatedIssueIds);
    }

    @Override
    public Object getDashBoardInfo(String duration, String project_id, String userToken,String category) {
        IssueCount result=new IssueCount(0,0,0);
        String account_id = restInterfaceManager.getAccountId(userToken);
        if (project_id == null||project_id.equals("")) {
            //未选择某一个project,显示该用户所有project的dashboard信息
            JSONArray repoIds = restInterfaceManager.getRepoIdsOfAccount(account_id,category);
            if (repoIds != null&&!repoIds.isEmpty()) {
                for (int i = 0; i < repoIds.size(); i++) {
                    String currentRepoId = repoIds.getString(i);
                    result.issueCountUpdate(getOneRepoDashBoardInfo(duration,currentRepoId,category));
                }
            }
        } else {
            //只显示当前所选project的相关dashboard信息
            String currentRepoId = restInterfaceManager.getRepoIdOfProject(project_id);
            return getOneRepoDashBoardInfo(duration,currentRepoId,category);
        }
        return result;
    }

    private List<IssueCount> getFormatData(List<String> newList, List<String> remainingList, List<String> eliminatedList) {
        if (newList == null || remainingList == null || eliminatedList == null) {
            return Collections.emptyList();
        }
        List<IssueCount> list = new ArrayList<>();
        for (int i = 0; i < newList.size(); i++) {
            String[] str1=newList.get(i).split(":");
            String[] str2=eliminatedList.get(i).split(":");
            String[] str3=remainingList.get(i).split(":");
            list.add(new IssueCount(str1[0],Integer.parseInt(str1[1]),Integer.parseInt(str2[1]),Integer.parseInt(str3[1])));
        }
        return list;
    }

    @Override
    public Object getStatisticalResults(Integer month, String project_id, String userToken,String category) {
        Map<String, Object> result = new HashMap<>();
        String account_id = restInterfaceManager.getAccountId(userToken);
        String newKey;
        String remainingKey;
        String eliminatedKey;
        if (project_id == null) {
            if (month == 1) {
                newKey = "trend:"+category+":day:new:" + account_id;
                remainingKey = "trend:"+category+":day:remaining:" + account_id;
                eliminatedKey = "trend:"+category+":day:eliminated:" + account_id;
            } else {
                newKey = "trend:"+category+":week:new:" + account_id;
                remainingKey = "trend:"+category+":week:remaining:" + account_id;
                eliminatedKey = "trend:"+category+":week:eliminated:" + account_id;
            }
        } else {
            String repoId = restInterfaceManager.getRepoIdOfProject(project_id);
            if (month == 1) {
                newKey = "trend:"+category+":day:new:" + account_id + ":" + repoId;
                remainingKey = "trend:"+category+":day:remaining:" + account_id + ":" + repoId;
                eliminatedKey = "trend:"+category+":day:eliminated:" + account_id + ":" + repoId;
            } else {
                newKey = "trend:"+category+":week:new:" + account_id + ":" + repoId;
                remainingKey = "trend:"+category+":week:remaining:" + account_id + ":" + repoId;
                eliminatedKey = "trend:"+category+":week:eliminated:" + account_id + ":" + repoId;
            }
        }
        List<String> newList = stringRedisTemplate.opsForList().range(newKey, 0, -1);
        List<String> remainingList = stringRedisTemplate.opsForList().range(remainingKey, 0, -1);
        List<String> eliminatedList = stringRedisTemplate.opsForList().range(eliminatedKey, 0, -1);
        result.put("data", getFormatData(newList, remainingList, eliminatedList));
        return result;
    }

    @Override
    public Object getExistIssueTypes(String category) {
        List<String> types=issueDao.getExistIssueTypes(category);
        if(category.equals("clone")){
            types.sort(Comparator.comparingInt(Integer::valueOf));
        }
        return types;
    }

    @Override
    public Object getAliveAndEliminatedInfo(String project_id, String category) {
        String repoId=restInterfaceManager.getRepoIdOfProject(project_id);
        JSONArray solvedIssueIds=restInterfaceManager.getSpecificTaggedIssueIds(solvedTagId);
        double avgEliminatedTime=0.00;
        long maxAliveTime=0;
        if(solvedIssueIds!=null&&!solvedIssueIds.isEmpty()){
            List<String> solvedIssueIdList=solvedIssueIds.toJavaList(String.class);
            Double avgEliminatedTimeObject=issueDao.getAvgEliminatedTime(solvedIssueIdList,repoId,category);
            avgEliminatedTime=avgEliminatedTimeObject==null?0.00:avgEliminatedTimeObject;
            maxAliveTime=issueDao.getMaxAliveTime(solvedIssueIdList,repoId,category);
        }else{
            //所有issue都是存活的
            maxAliveTime=issueDao.getMaxAliveTime(null,repoId,category);
        }
        return new IssueStatisticInfo(avgEliminatedTime/3600/24,maxAliveTime/3600/24);
    }

    @Override
    public void startMapping(String repo_id, String pre_commit_id, String current_commit_id,String category) {
        String committer="";
        JSONObject repoPathJson = null;
        String repoPath = null;
        JGitHelper jGitHelper = null;
        try{
            repoPathJson = restInterfaceManager.getRepoPath(repo_id,current_commit_id);
            if(repoPathJson == null){
                throw new RuntimeException("can not get repo path");
            }
            repoPath = repoPathJson.getJSONObject("data").getString("content");
            if(repoPath != null){
                jGitHelper = new JGitHelper(repoPath);
                committer = jGitHelper.getAuthorName(current_commit_id);
            }
        }finally{
            if(repoPath!= null){
                restInterfaceManager.freeRepoPath(repo_id,repoPath);
            }
        }

        if(category.equals("bug")){
            logger.info("start bug mapping -> repo_id={},pre_commit_id={},current_commit_id={}",repo_id,pre_commit_id,current_commit_id);
            bugMappingService.mapping(repo_id,pre_commit_id,current_commit_id,category,committer);
        }else if(category.equals("clone")){
            logger.info("start clone mapping -> repo_id={},pre_commit_id={},current_commit_id={}",repo_id,pre_commit_id,current_commit_id);
            cloneMappingService.mapping(repo_id,pre_commit_id,current_commit_id,category,committer);
        }else if(category.equals("sonar")){
            logger.info("start sonar mapping -> repo_id={},pre_commit_id={},current_commit_id={}",repo_id,pre_commit_id,current_commit_id);
            sonarMappingService.mapping(repo_id,pre_commit_id,current_commit_id,category,committer);
        }
    }

    @Override
    public void updateIssueCount(String time) {
        quartzScheduler.updateIssueCount(time);
    }

    @Override
    public Object getNewTrend(Integer month, String project_id, String userToken, String category) {
        List<IssueCountPo> result=new ArrayList<>();
        String account_id = restInterfaceManager.getAccountId(userToken);
        LocalDate end=LocalDate.now();
        if(project_id==null||project_id.equals("")){
            //需要查询该用户所有项目的扫描情况
            JSONArray repoIds=restInterfaceManager.getRepoIdsOfAccount(account_id,category);
            if(repoIds!=null&&!repoIds.isEmpty()){
                if(month==1){
                    //过去30天
                    LocalDate start=end.minusMonths(1);
                    return scanResultDao.getScanResultsGroupByDay(repoIds.toJavaList(String.class),category, DateTimeUtil.y_m_d_format(start),DateTimeUtil.y_m_d_format(end));
                }else if(month==3||month==6){
                    //过去3个月
                    LocalDate start=end.minusMonths(month);
                    while(start.isBefore(end)){
                        LocalDate temp=start.plusWeeks(1);
                        IssueCountPo issueCountPo=scanResultDao.getMergedScanResult(repoIds.toJavaList(String.class),category, DateTimeUtil.y_m_d_format(start),DateTimeUtil.y_m_d_format(temp));
                        if(issueCountPo!=null) {
                            result.add(issueCountPo);
                        }
                        start=temp;
                    }
                }else{
                    throw new IllegalArgumentException("month should be 1 or 3 or 6");
                }
            }
        }else{
            //只需要查询该项目的扫描情况
            String repoId=restInterfaceManager.getRepoIdOfProject(project_id);
            if(repoId==null) {
                throw new IllegalArgumentException("this project id not exist!");
            }
            if(month==1){
                //过去30天
                LocalDate start=end.minusMonths(1);
                return scanResultDao.getScanResultsGroupByDay(Collections.singletonList(repoId),category, DateTimeUtil.y_m_d_format(start),DateTimeUtil.y_m_d_format(end));
            }else if(month==3||month==6){
                //过去3个月
                LocalDate start=end.minusMonths(month);
                while(start.isBefore(end)){
                    LocalDate temp=start.plusWeeks(1);
                    IssueCountPo issueCountPo=scanResultDao.getMergedScanResult(Collections.singletonList(repoId),category, DateTimeUtil.y_m_d_format(start),DateTimeUtil.y_m_d_format(temp));
                    if(issueCountPo!=null) {
                        result.add(issueCountPo);
                    }
                    start=temp;
                }
            }else{
                throw new IllegalArgumentException("month should be 1 or 3 or 6");
            }
        }
        return result;
    }

    @Override
    public void updatePriority(String issueId, String priority,String token) {
        int priorityInt = Integer.parseInt(priority);
        issueDao.updateOneIssuePriority(issueId,priorityInt);
        String priorityTag = getPriorityTagIdByIntValue(priorityInt,token);
        String preTagId = restInterfaceManager.getTagIdByItemIdAndScope(issueId,"priority");
        List<JSONObject> taggeds = new ArrayList<>();
        JSONObject tagged = new JSONObject();
        tagged.put("itemId", issueId);
        tagged.put("preTagId", preTagId);
        tagged.put("newTagId", priorityTag);
        taggeds.add(tagged);
        restInterfaceManager.modifyTags(taggeds);
    }

    @Override
    public void updateStatus(String issueId, String status,String token) {
        issueDao.updateOneIssueStatus(issueId, status, status);
        String statusTag = issueUtil.getTagIdByStatus(status);
        String preTagId = restInterfaceManager.getTagIdByItemIdAndScope(issueId, "status");
        List<JSONObject> taggeds = new ArrayList<>();
        JSONObject tagged = new JSONObject();
        tagged.put("itemId", issueId);
        tagged.put("preTagId", preTagId);
        tagged.put("newTagId", statusTag);
        taggeds.add(tagged);
        restInterfaceManager.modifyTags(taggeds);
    }

    @Override
    public void updateIssueType(String issueId, String type, String token,String tool) {
        String scope = null;

        if("findbugs".equals(tool)){
            scope = "findbugs_type";
        }else if("sonar".equals(tool)){
            scope = "sonar_type";
        }
        String preTagId = restInterfaceManager.getTagIdByItemIdAndScope(issueId, scope);
        List<JSONObject> taggeds = new ArrayList<>();
        JSONObject tagged = new JSONObject();
        tagged.put("itemId", issueId);
        tagged.put("preTagId", preTagId);
        JSONArray issueTypes = restInterfaceManager.getTagByCondition(null, type,scope);
        String issueTypeTagId = null;
        if(issueTypes.size() == 1){
            issueTypeTagId = issueTypes.getJSONObject(0).getString("uuid");
        }
        tagged.put("newTagId", issueTypeTagId);
        taggeds.add(tagged);
        restInterfaceManager.modifyTags(taggeds);
    }

    @Override
    public Object getRepoIssueCounts(String repo_id, String since, String until, String category) {
//        LocalDate indexDay = LocalDate.parse(since,DateTimeUtil.Y_M_D_formatter);
//        LocalDate untilDay = LocalDate.parse(until,DateTimeUtil.Y_M_D_formatter);
//        while(untilDay.isAfter(indexDay) || untilDay.isEqual(indexDay)){
//            Map<String, Object> map = scanResultDao.getRepoIssueCounts(repo_id, indexDay.toString(), indexDay.toString(), category, null);
//            if (map.get("commit_date") == null){
//                map.put("commit_date", indexDay.toString());
//            }
//            result.add(map);
//            indexDay = indexDay.plusDays(1);
//        }
        return scanResultDao.getScanResultsGroupByDay(Collections.singletonList(repo_id),category, since, until);
    }



    @Override
    public void batchUpdateIssueListPriority(List<String> issueUuid, Integer priority) {
        issueDao.batchUpdateIssueListPriority(issueUuid, priority.intValue());
    }

    @Override
    public void batchUpdateIssueListStatus(List<String> issueUuid, String status) {
        issueDao.batchUpdateIssueListStatus(issueUuid, status);
    }

    @Override
    public List<String> getNotSolvedIssueListByTypeAndRepoId(String repoId, String type) {
        return issueDao.getNotSolvedIssueListByTypeAndRepoId(repoId, type);
    }

    @Override
    public Issue getIssueByIssueId(String issueId) {
        return issueDao.getIssueByIssueId(issueId);
    }

    private String getPriorityTagIdByIntValue(int priorityInt, String token){
        String priorityTagId = null;
        JSONArray tagList = restInterfaceManager.getTagByScope("priority", token);
        String lowTagId = null;
        String urgentTagId = null;
        String normalTagId = null;
        String highTagId = null;
        String immediateTagId = null;
        for (int i = 0; i < tagList.size(); i++) {
            JSONObject tagJson = tagList.getJSONObject(i);
            String tagIdFromJson = tagJson.getString("uuid");
            switch (PriorityEnum.getPriorityEnum(tagJson.getString("name"))) {
                case LOW:
                    lowTagId = tagIdFromJson;
                    break;
                case URGENT:
                    urgentTagId = tagIdFromJson;
                    break;
                case NORMAL:
                    normalTagId = tagIdFromJson;
                    break;
                case HIGH:
                    highTagId = tagIdFromJson;
                    break;
                case IMMEDIATE:
                    immediateTagId = tagIdFromJson;
                    break;
                default:

            }
        }
        switch (priorityInt) {
            case 0:
                priorityTagId = immediateTagId;
                break;
            case 1:
                priorityTagId = urgentTagId;
                break;
            case 2:
                priorityTagId = highTagId;
                break;
            case 3:
                priorityTagId = normalTagId;
                break;
            case 4:
                priorityTagId = lowTagId;
                break;
            default:
                priorityTagId = null;
        }
        return priorityTagId;
    }

}
