package cn.edu.fudan.issueservice.service;

import cn.edu.fudan.issueservice.IssueServiceApplicationTests;
import cn.edu.fudan.issueservice.component.RestInterfaceManager;
import cn.edu.fudan.issueservice.dao.IssueDao;
import cn.edu.fudan.issueservice.dao.RawIssueDao;
import cn.edu.fudan.issueservice.domain.Issue;
import cn.edu.fudan.issueservice.domain.IssueCount;
import cn.edu.fudan.issueservice.domain.RawIssue;
import cn.edu.fudan.issueservice.domain.ResponseBean;
import cn.edu.fudan.issueservice.service.impl.BaseMappingServiceImpl;
import cn.edu.fudan.issueservice.service.impl.IssueServiceImpl;
import cn.edu.fudan.issueservice.util.TestDataMaker;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.ListMap;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@PrepareForTest({IssueService.class, IssueServiceImpl.class, IssueDao.class, RawIssueDao.class,RestTemplate.class, StringRedisTemplate.class,HashOperations.class, ResponseEntity.class, ListOperations.class, BaseMappingServiceImpl.class})
public class IssueServiceTest extends IssueServiceApplicationTests {

    @Value("${tag.service.path}")
    private String tagServicePath;
    @Value("${solved.tag_id}")
    private String solvedTagId;
    @Value("${ignore.tag_id}")
    private String ignoreTagId;


    @Value("${commit.service.path}")
    private String commitServicePath;


    @Autowired
    private RawIssueDao rawIssueDao;

    @Mock
    private IssueDao issueDao;

    @Mock
    private RestInterfaceManager restInterfaceManager;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private HashOperations hashOperations;

    @Mock
    private ListOperations listOperations;

    @Mock
    private ResponseEntity responseEntity;

    @Mock
    private ResponseEntity accountIdResponseEntity;

    @Mock
    private ResponseEntity repoIdResponseEntity;

    @Mock
    private BaseMappingServiceImpl baseMappingService;


    @Autowired
    @InjectMocks
    private IssueService issueService = new IssueServiceImpl();

    private TestDataMaker testDataMaker;

    List<Issue> list;
    Issue issue1;
    Issue issue2;

    List<RawIssue> rawIssues1;
    List<RawIssue> rawIssues2;
    RawIssue rawIssue1;
    RawIssue rawIssue2;
    RawIssue rawIssue3;

    Map<String, Object> map;

    @Before
    public void setup() throws Exception {
        rawIssueDao = Mockito.mock(RawIssueDao.class);
        testDataMaker = new TestDataMaker();

        issue1 = testDataMaker.issueMaker1();
        issue2 = testDataMaker.issueMaker2();
        list = new ArrayList<>();
        list.add(issue1);
        list.add(issue2);
        rawIssue1 = testDataMaker.rawIssueMaker1();
        rawIssue2 = testDataMaker.rawIssueMaker2();
        rawIssue3 = testDataMaker.rawIssueMaker3();
        rawIssues1 = new ArrayList<>();
        rawIssues1.add(rawIssue1);
        rawIssues2 = new ArrayList<>();
        rawIssues2.add(rawIssue2);
        rawIssues2.add(rawIssue3);
        MemberModifier.field(IssueServiceImpl.class, "issueDao").set(issueService, issueDao);
        MemberModifier.field(IssueServiceImpl.class, "restInterfaceManager").set(issueService, restInterfaceManager);
        MemberModifier.field(IssueServiceImpl.class, "stringRedisTemplate").set(issueService, stringRedisTemplate);
        System.out.println("finish mocking");



        map = new HashMap<String, Object>();
        map.put("project_id", "222");
        map.put("type", "OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE");
        map.put("start", 0);
        map.put("size", 2);
        map.put("page", 1);

    }

    @Test
    public void insertIssueList() {
        doNothing().when(issueDao).insertIssueList(list);
        issueService.insertIssueList(list);
        verify(issueDao, Mockito.atLeastOnce()).insertIssueList(list);
    }

    @Test
    @Ignore
    public void deleteIssueByProjectId() throws Exception {
        String repo_id = "repo_id";
        List<String> stringList = new ArrayList<String>();
        stringList.add("iss1");
        stringList.add("iss2");
        PowerMockito.when(issueDao.getIssueIdsByRepoIdAndCategory(repo_id,"bug")).thenReturn(stringList);
        /*
            删除失败
         */
        PowerMockito.doThrow(new RuntimeException("tag item delete failed!")).when(restInterfaceManager,"deleteTagsOfIssueInOneRepo",stringList);
        try{
            issueService.deleteIssueByRepoIdAndCategory(repo_id,"bug");
        }catch(RuntimeException e){
            Assert.assertEquals("tag item delete failed!",e.getMessage());
        }
        /*
            当返回的JSONObject的code代码是200
         */
        PowerMockito.doNothing().when(issueDao,"deleteIssueByRepoIdAndCategory",repo_id,"bug");
        PowerMockito.doNothing().when(restInterfaceManager,"deleteTagsOfIssueInOneRepo",stringList);
        issueService.deleteIssueByRepoIdAndCategory(repo_id,"bug");
        verify(issueDao,Mockito.atLeastOnce()).deleteIssueByRepoIdAndCategory(repo_id,"bug");

    }

    @Test
    public void batchUpdateIssue() {
        doNothing().when(issueDao).batchUpdateIssue(list);
        issueService.batchUpdateIssue(list);
        verify(issueDao, Mockito.atLeastOnce()).batchUpdateIssue(list);
    }

    @Test
    public void getIssueByID() {
        String issueId = "iss1";
        PowerMockito.when(issueDao.getIssueByID(issueId)).thenReturn(issue1);
        Issue issue = issueService.getIssueByID(issueId);
        Assert.assertEquals(issue1.getUuid(),issue.getUuid());
    }

    @Test
    public void getIssueList() {
        int count = 2;
        List<String> itemIds = new ArrayList<>();
        itemIds.add("item1");
        itemIds.add("item2");
        JSONArray solved_issue_ids = JSON.parseArray(JSONObject.toJSON(itemIds).toString());

        String repo_id = "repo_id";
        String projectId = "pro1";
        Integer page = 1;
        Integer size = 2;
        String category = "category";
        List<String> tag_ids = new ArrayList<>();
        tag_ids.add(solvedTagId);
        tag_ids.add(ignoreTagId);

        PowerMockito.when(restInterfaceManager.getRepoIdOfProject(projectId)).thenReturn(repo_id);
        PowerMockito.when(restInterfaceManager.getSolvedIssueIds(tag_ids)).thenReturn(solved_issue_ids);
        PowerMockito.when(issueDao.getIssueCount(any(Map.class))).thenReturn(count);
        PowerMockito.when(issueDao.getIssueList(any(Map.class))).thenReturn(list);

        Map<String, Object> result = (Map<String, Object>) issueService.getIssueList(projectId, page, size,category);
        Assert.assertEquals(1,result.get("totalPage"));
        Assert.assertEquals(2,result.get("totalCount"));
        List<Issue> listResult = (List<Issue>)result.get("issueList");
        Assert.assertEquals(list.size(), listResult.size());
        for (int i = 0; i < listResult.size(); ++i) {
            Assert.assertEquals(list.get(i),listResult.get(i));
        }
    }

    @Test
    public void getFilteredIssueList() {
        int count = 2;
        String project_id = "project_id";
        String repo_id = "repo_id";
        int size = 2;
        String category = "category";
        JSONObject requestParam = new JSONObject();
        requestParam.put("project_id",project_id);
        requestParam.put("size",size);
        requestParam.put("category",category);

        PowerMockito.when(restInterfaceManager.getRepoIdOfProject(project_id)).thenReturn(repo_id);

        String issueId1= "issueId1";
        String issueId2= "issueId2";
        List<String> listIssueIds = new ArrayList<String>();
        listIssueIds.add(issueId1);
        listIssueIds.add(issueId2);
        JSONArray IssueIds = JSON.parseArray(JSONObject.toJSON(listIssueIds).toString());

        Map<String, Object> result;
        JSONArray tags = new JSONArray();
        /*
           测试一：当page参数缺失时报错
         */
        try{
            result = (Map<String, Object>)issueService.getFilteredIssueList(requestParam);
        }catch(IllegalArgumentException e){
            Assert.assertEquals("param lost!",e.getMessage());
        }
        //参数补充完全
        int page = 1;
        requestParam.put("page",page);
        /*
            测试二：当tag的筛选条件不为空，且tag的筛选结果为空时
         */
        String tagId1= "tagId1";
        String tagId2= "tagId2";
        List<String> listTagIds = new ArrayList<String>();
        listTagIds.add(tagId1);
        listTagIds.add(tagId2);
        requestParam.put("tags",listTagIds);
        JSONArray tag_ids = JSON.parseArray(JSONObject.toJSON(listTagIds).toString());
        PowerMockito.when(restInterfaceManager.getSpecificTaggedIssueIds(tag_ids)).thenReturn(null);
        result = (Map<String, Object>)issueService.getFilteredIssueList(requestParam);
        Assert.assertEquals(0,result.get("totalPage"));
        Assert.assertEquals(0,result.get("totalCount"));
        Assert.assertEquals(Collections.emptyList(),result.get("issueList"));
        /*
            测试三：当tag的筛选条件不为空，tag的筛选结果不为空时，且type的筛选条件不为空
         */
        String type1= "type1";
        String type2= "type2";
        List<String> listTypes = new ArrayList<String>();
        listTypes.add(type1);
        listTypes.add(type2);
        requestParam.put("types",listTypes);
        PowerMockito.when(restInterfaceManager.getSpecificTaggedIssueIds(tag_ids)).thenReturn(IssueIds);

        PowerMockito.when(issueDao.getIssueCount(any(Map.class))).thenReturn(count);
        PowerMockito.when(issueDao.getIssueList(any(Map.class))).thenReturn(list);
       // PowerMockito.when(restTemplate.getForObject(tagServicePath + "?item_id=" + list.get(0).getUuid(), JSONArray.class)).thenReturn(tags);
       // PowerMockito.when(restTemplate.getForObject(tagServicePath + "?item_id=" + list.get(1).getUuid(), JSONArray.class)).thenReturn(tags);

        result = (Map<String, Object>)issueService.getFilteredIssueList(requestParam);
        Assert.assertEquals(1,result.get("totalPage"));
        Assert.assertEquals(2,result.get("totalCount"));
        List<Issue> listResult = (List<Issue>)result.get("issueList");
        Assert.assertEquals(list.size(), listResult.size());
        for (int i = 0; i < listResult.size(); ++i) {
            Assert.assertEquals(list.get(i),listResult.get(i));
        }
    }

    @Test
    public void getDashBoardInfo() {
        String repoId1 = "repoId1";
        String repoId2= "repoId2";
        String duration = "duration";
        String project_id = "proId";
        String userToken = "token";
        String category = "category";
        String account_id = "acc_id";
        PowerMockito.when(restInterfaceManager.getAccountId(userToken)).thenReturn(account_id);

        List<String> listRepoIds = new ArrayList<String>();
        listRepoIds.add(repoId1);
        listRepoIds.add(repoId2);
        JSONArray repoIds = JSON.parseArray(JSONObject.toJSON(listRepoIds).toString());

        PowerMockito.when(stringRedisTemplate.opsForHash()).thenReturn(hashOperations);
        PowerMockito.when(stringRedisTemplate.opsForList()).thenReturn(listOperations);
        Object newIssueCount = "1";
        Object eliminatedIssueCount = "2";
        Object remainingIssueCount = "3";
        PowerMockito.when(hashOperations.get("dashboard:"+category+":"+ duration + ":" + repoId1, "new")).thenReturn(newIssueCount);
        PowerMockito.when(hashOperations.get("dashboard:"+category+":"+ duration + ":" + repoId1, "remaining")).thenReturn(remainingIssueCount);
        PowerMockito.when(hashOperations.get("dashboard:"+category+":"+ duration + ":" + repoId1, "eliminated")).thenReturn(eliminatedIssueCount);
        PowerMockito.when(listOperations.range("dashboard:"+category+":"+duration+":new"+ repoId1,0,-1)).thenReturn(null);
        PowerMockito.when(hashOperations.get("dashboard:"+category+":"+ duration + ":" + repoId2, "new")).thenReturn(newIssueCount);
        PowerMockito.when(hashOperations.get("dashboard:"+category+":"+ duration + ":" + repoId2, "remaining")).thenReturn(remainingIssueCount);
        PowerMockito.when(hashOperations.get("dashboard:"+category+":"+ duration + ":" + repoId2, "eliminated")).thenReturn(eliminatedIssueCount);
        PowerMockito.when(listOperations.range("dashboard:"+category+":"+duration+":new"+ repoId2,0,-1)).thenReturn(null);
        /*
            当project_id为null时，且当用户没有添加project时
         */
        PowerMockito.when(restInterfaceManager.getRepoIdsOfAccount(account_id,category)).thenReturn(null);
        IssueCount result = (IssueCount)issueService.getDashBoardInfo(duration,null,userToken,category);
        Assert.assertEquals(0,result.getNewIssueCount());
        Assert.assertEquals(0,result.getEliminatedIssueCount());
        Assert.assertEquals(0,result.getRemainingIssueCount());
        /*
             当project_id为null时，且当用户有添加project时
         */
        PowerMockito.when(restInterfaceManager.getRepoIdsOfAccount(account_id,category)).thenReturn(repoIds);
        IssueCount resultAddProject = (IssueCount)issueService.getDashBoardInfo(duration,null,userToken,category);
        Assert.assertEquals(2,resultAddProject.getNewIssueCount());
        Assert.assertEquals(4,resultAddProject.getEliminatedIssueCount());
        Assert.assertEquals(6,resultAddProject.getRemainingIssueCount());
        /*
             当project_id为null时，且当用户有添加project时
         */
        PowerMockito.when(restInterfaceManager.getRepoIdOfProject(project_id)).thenReturn(repoId1);
        IssueCount resultProjectIsNull = (IssueCount)issueService.getDashBoardInfo(duration,project_id,userToken,category);
        Assert.assertEquals(1,resultProjectIsNull.getNewIssueCount());
        Assert.assertEquals(2,resultProjectIsNull.getEliminatedIssueCount());
        Assert.assertEquals(3,resultProjectIsNull.getRemainingIssueCount());
    }

    @Test
    public void getStatisticalResults() {
        /*
            当month为1，且project_id为null
         */
        IssueCount issueCount1 = new IssueCount();
        issueCount1.setNewIssueCount(1);
        issueCount1.setRemainingIssueCount(3);
        issueCount1.setEliminatedIssueCount(2);
        IssueCount issueCount2 = new IssueCount();
        issueCount2.setNewIssueCount(2);
        issueCount2.setRemainingIssueCount(2);
        issueCount2.setEliminatedIssueCount(3);
        IssueCount issueCount3 = new IssueCount();
        issueCount3.setNewIssueCount(3);
        issueCount3.setRemainingIssueCount(1);
        issueCount3.setEliminatedIssueCount(4);
        List<IssueCount> issueCounts = new ArrayList<>();
        issueCounts.add(issueCount1);
        issueCounts.add(issueCount2);
        issueCounts.add(issueCount3);

        Map map = new ListMap();
        map.put("data",issueCounts);
        List<String> newList = new ArrayList<>();
        newList.add("xxx:1");
        newList.add("xxx:2");
        newList.add("xxx:3");
        List<String> remainingList = new ArrayList<>();
        remainingList.add("xxx:3");
        remainingList.add("xxx:2");
        remainingList.add("xxx:1");
        List<String> eliminatedList = new ArrayList<>();
        eliminatedList.add("xxx:2");
        eliminatedList.add("xxx:3");
        eliminatedList.add("xxx:4");
        Integer month = 1;
        String project_id = "project_id";
        String userToken = "userToken";
        String category = "category";
        String account_id = "account_id";
        PowerMockito.when(restInterfaceManager.getAccountId(userToken)).thenReturn(account_id);

        PowerMockito.when(stringRedisTemplate.opsForList()).thenReturn(listOperations);
        PowerMockito.when(listOperations.range("trend:"+category+":day:new:" + account_id, 0, -1)).thenReturn(newList);
        PowerMockito.when(listOperations.range("trend:"+category+":day:remaining:" + account_id, 0, -1)).thenReturn(remainingList);
        PowerMockito.when(listOperations.range("trend:"+category+":day:eliminated:" + account_id, 0, -1)).thenReturn(eliminatedList);
        Map resultMap = (Map) issueService.getStatisticalResults(month,null,userToken,category);
        List<IssueCount> listResult = (List<IssueCount>)resultMap.get("data");
        Assert.assertEquals(issueCounts.size(),listResult.size());
        for(int i=0;i<listResult.size();++i){
            Assert.assertEquals(issueCounts.get(i).toString(),listResult.get(i).toString());
        }

        /*
            当month不为1，且project_id为null
         */

        month = 3;
        PowerMockito.when(stringRedisTemplate.opsForList()).thenReturn(listOperations);
        PowerMockito.when(listOperations.range("trend:"+category+":week:new:" + account_id, 0, -1)).thenReturn(newList);
        PowerMockito.when(listOperations.range("trend:"+category+":week:remaining:" + account_id, 0, -1)).thenReturn(remainingList);
        PowerMockito.when(listOperations.range("trend:"+category+":week:eliminated:" + account_id, 0, -1)).thenReturn(eliminatedList);

        resultMap = (Map) issueService.getStatisticalResults(month,null,userToken,category);
        listResult = (List<IssueCount>)resultMap.get("data");
        Assert.assertEquals(issueCounts.size(),listResult.size());
        for(int i=0;i<listResult.size();++i){
            Assert.assertEquals(issueCounts.get(i).toString(),listResult.get(i).toString());
        }

        /*
            当month不为1，且project_id为null
         */
        String repoId = "repoId";
        month = 1;
        PowerMockito.when(restInterfaceManager.getRepoIdOfProject(project_id)).thenReturn(repoId);
        PowerMockito.when(stringRedisTemplate.opsForList()).thenReturn(listOperations);
        PowerMockito.when(listOperations.range("trend:"+category+":day:new:" + account_id + ":" + repoId, 0, -1)).thenReturn(newList);
        PowerMockito.when(listOperations.range("trend:"+category+":day:remaining:" + account_id + ":" + repoId, 0, -1)).thenReturn(remainingList);
        PowerMockito.when(listOperations.range("trend:"+category+":day:eliminated:" + account_id + ":" + repoId, 0, -1)).thenReturn(eliminatedList);
        resultMap = (Map) issueService.getStatisticalResults(month,project_id,userToken,category);
        listResult = (List<IssueCount>)resultMap.get("data");
        Assert.assertEquals(issueCounts.size(),listResult.size());
        for(int i=0;i<listResult.size();++i){
            Assert.assertEquals(issueCounts.get(i).toString(),listResult.get(i).toString());
        }
        /*
            当month不为1，且project_id为null
         */
        month = 3;
        PowerMockito.when(stringRedisTemplate.opsForList()).thenReturn(listOperations);
        PowerMockito.when(listOperations.range("trend:"+category+":week:new:" + account_id + ":" + repoId, 0, -1)).thenReturn(newList);
        PowerMockito.when(listOperations.range("trend:"+category+":week:remaining:" + account_id + ":" + repoId, 0, -1)).thenReturn(remainingList);
        PowerMockito.when(listOperations.range("trend:"+category+":week:eliminated:" + account_id + ":" + repoId, 0, -1)).thenReturn(eliminatedList);
        resultMap = (Map) issueService.getStatisticalResults(month,project_id,userToken,category);
        listResult = (List<IssueCount>)resultMap.get("data");
        Assert.assertEquals(issueCounts.size(),listResult.size());
        for(int i=0;i<listResult.size();++i){
            Assert.assertEquals(issueCounts.get(i).toString(),listResult.get(i).toString());
        }
    }

    @Test
    public void getExistIssueTypes() {
        String category = "category";
        String type1 = "EI_EXPOSE_REP";
        String type2 = "EI_EXPOSE_REP2";
        List<String> types = new ArrayList<String>();
        types.add(type1);
        types.add(type2);
        PowerMockito.when(issueDao.getExistIssueTypes(category)).thenReturn(types);

        List<String> resultTypes = (List<String>)issueService.getExistIssueTypes(category);
        Assert.assertEquals(types.size(),resultTypes.size());
        for (int i = 0; i < resultTypes.size(); ++i) {
            Assert.assertEquals(types.get(i),resultTypes.get(i));
        }

    }


    /*
        只验证了bugmapping
     */
    @Test
    @Ignore
    public void startMapping() {

    }
    @Test
    @Ignore
    public void updateIssueCount() {

    }
}