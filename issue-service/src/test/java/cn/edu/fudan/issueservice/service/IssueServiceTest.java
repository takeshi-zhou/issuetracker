package cn.edu.fudan.issueservice.service;

import cn.edu.fudan.issueservice.IssueServiceApplicationTests;
import cn.edu.fudan.issueservice.dao.IssueDao;
import cn.edu.fudan.issueservice.domain.Issue;
import cn.edu.fudan.issueservice.domain.IssueCount;
import cn.edu.fudan.issueservice.domain.ResponseBean;
import cn.edu.fudan.issueservice.service.impl.IssueServiceImpl;
import cn.edu.fudan.issueservice.util.TestDataMaker;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@PrepareForTest({IssueService.class, IssueServiceImpl.class, IssueDao.class,RestTemplate.class, StringRedisTemplate.class,HashOperations.class})
public class IssueServiceTest extends IssueServiceApplicationTests {

    @Value("${tag.service.path}")
    private String tagServicePath;
    @Value("${solved.tag_id}")
    private String solvedTagId;
    @Value("${ignore.tag_id}")
    private String ignoreTagId;

    @Mock
    private IssueDao issueDao;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private HashOperations hashOperations;

    @Autowired
    @InjectMocks
    private IssueService issueService = new IssueServiceImpl();

    private TestDataMaker testDataMaker;

    List<Issue> list;
    Issue issue1;
    Issue issue2;
    Map<String, Object> map;

    @Before
    public void setup() throws Exception {
        testDataMaker = new TestDataMaker();

        issue1 = testDataMaker.issueMaker1();
        issue2 = testDataMaker.issueMaker2();
        list = new ArrayList<>();
        list.add(issue1);
        list.add(issue2);
        MemberModifier.field(IssueServiceImpl.class, "issueDao").set(issueService, issueDao);
        MemberModifier.field(IssueServiceImpl.class, "restTemplate").set(issueService, restTemplate);
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
    public void deleteIssueByProjectId() {
        String repo_id = "repo_id";
        List<String> stringList = new ArrayList<String>();
        stringList.add("iss1");
        stringList.add("iss2");
        PowerMockito.when(issueDao.getIssueIdsByRepoId(repo_id)).thenReturn(stringList);
        ResponseBean responseBean = new ResponseBean();
        responseBean.setCode(401);
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSON(responseBean).toString());
        /*
            当返回的JSONObject为null
         */
        PowerMockito.when(restTemplate.postForObject(tagServicePath+"/tagged-delete",stringList, JSONObject.class)).thenReturn(null);
        try{
            issueService.deleteIssueByRepoId(repo_id);
        }catch(RuntimeException e){
            Assert.assertEquals("tag item delete failed!",e.getMessage());
        }
        /*
            当返回的JSONObject的code代码不是200
         */
        PowerMockito.when(restTemplate.postForObject(tagServicePath+"/tagged-delete",stringList, JSONObject.class)).thenReturn(jsonObject);
        try{
            issueService.deleteIssueByRepoId(repo_id);
        }catch(RuntimeException e){
            Assert.assertEquals("tag item delete failed!",e.getMessage());
        }
        /*
            当返回的JSONObject的code代码是200
         */
        responseBean.setCode(200);
        jsonObject = JSONObject.parseObject(JSONObject.toJSON(responseBean).toString());
        PowerMockito.when(restTemplate.postForObject(tagServicePath+"/tagged-delete",stringList, JSONObject.class)).thenReturn(jsonObject);
        issueService.deleteIssueByRepoId(repo_id);
        verify(issueDao,Mockito.atLeastOnce()).deleteIssueByRepoId(repo_id);

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
        JSONArray tags = new JSONArray();

        PowerMockito.when(restTemplate.getForObject(tagServicePath + "?item_id=" + list.get(1).getUuid(), JSONArray.class)).thenReturn(tags);
        PowerMockito.when(restTemplate.postForObject(tagServicePath+"/item-ids",tag_ids, JSONArray.class)).thenReturn(solved_issue_ids);
        MemberModifier.stub(MemberMatcher.method(IssueServiceImpl.class, "getRepoId")).toReturn(repo_id);
        PowerMockito.when(issueDao.getIssueCount(any(Map.class))).thenReturn(count);
        PowerMockito.when(issueDao.getIssueList(any(Map.class))).thenReturn(list);
        PowerMockito.when(restTemplate.getForObject(tagServicePath + "?item_id=" + list.get(0).getUuid(), JSONArray.class)).thenReturn(tags);
        PowerMockito.when(restTemplate.getForObject(tagServicePath + "?item_id=" + list.get(1).getUuid(), JSONArray.class)).thenReturn(tags);

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

        MemberModifier.stub(MemberMatcher.method(IssueServiceImpl.class, "getRepoId")).toReturn(repo_id);

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
        PowerMockito.when(restTemplate.postForObject(tagServicePath + "/item-ids", tag_ids,JSONArray.class)).thenReturn(null);
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
        PowerMockito.when(restTemplate.postForObject(tagServicePath + "/item-ids", tag_ids,JSONArray.class)).thenReturn(IssueIds);
        PowerMockito.when(issueDao.getIssueCount(any(Map.class))).thenReturn(count);
        PowerMockito.when(issueDao.getIssueList(any(Map.class))).thenReturn(list);
        PowerMockito.when(restTemplate.getForObject(tagServicePath + "?item_id=" + list.get(0).getUuid(), JSONArray.class)).thenReturn(tags);
        PowerMockito.when(restTemplate.getForObject(tagServicePath + "?item_id=" + list.get(1).getUuid(), JSONArray.class)).thenReturn(tags);

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
        MemberModifier.stub(MemberMatcher.method(IssueServiceImpl.class, "getAccountId")).toReturn(account_id);

        List<String> listRepoIds = new ArrayList<String>();
        listRepoIds.add(repoId1);
        listRepoIds.add(repoId2);
        JSONArray repoIds = JSON.parseArray(JSONObject.toJSON(listRepoIds).toString());
        MemberModifier.stub(MemberMatcher.method(IssueServiceImpl.class, "getRepoId")).toReturn(repoId1);
        PowerMockito.when(stringRedisTemplate.opsForHash()).thenReturn(hashOperations);
        Object newIssueCount = "1";
        Object eliminatedIssueCount = "2";
        Object remainingIssueCount = "3";
        PowerMockito.when(hashOperations.get("dashboard:"+category+":"+ duration + ":" + repoId1, "new")).thenReturn(newIssueCount);
        PowerMockito.when(hashOperations.get("dashboard:"+category+":"+ duration + ":" + repoId1, "remaining")).thenReturn(remainingIssueCount);
        PowerMockito.when(hashOperations.get("dashboard:"+category+":"+ duration + ":" + repoId1, "eliminated")).thenReturn(eliminatedIssueCount);
        PowerMockito.when(hashOperations.get("dashboard:"+category+":"+ duration + ":" + repoId2, "new")).thenReturn(newIssueCount);
        PowerMockito.when(hashOperations.get("dashboard:"+category+":"+ duration + ":" + repoId2, "remaining")).thenReturn(remainingIssueCount);
        PowerMockito.when(hashOperations.get("dashboard:"+category+":"+ duration + ":" + repoId2, "eliminated")).thenReturn(eliminatedIssueCount);
        /*
            当project_id为null时，且当用户没有添加project时
         */
        MemberModifier.stub(MemberMatcher.method(IssueServiceImpl.class, "getRepoIds")).toReturn(null);
        IssueCount result = (IssueCount)issueService.getDashBoardInfo(duration,null,userToken,category);
        Assert.assertEquals(0,result.getNewIssueCount());
        Assert.assertEquals(0,result.getEliminatedIssueCount());
        Assert.assertEquals(0,result.getRemainingIssueCount());
        /*
             当project_id为null时，且当用户有添加project时
         */
        MemberModifier.stub(MemberMatcher.method(IssueServiceImpl.class, "getRepoIds")).toReturn(repoIds);
        IssueCount resultAddProject = (IssueCount)issueService.getDashBoardInfo(duration,null,userToken,category);
        Assert.assertEquals(2,resultAddProject.getNewIssueCount());
        Assert.assertEquals(4,resultAddProject.getEliminatedIssueCount());
        Assert.assertEquals(6,resultAddProject.getRemainingIssueCount());
        /*
             当project_id为null时，且当用户有添加project时
         */
        MemberModifier.stub(MemberMatcher.method(IssueServiceImpl.class, "getRepoIds")).toReturn(repoIds);
        IssueCount resultProjectIsNull = (IssueCount)issueService.getDashBoardInfo(duration,project_id,userToken,category);
        Assert.assertEquals(1,resultProjectIsNull.getNewIssueCount());
        Assert.assertEquals(2,resultProjectIsNull.getEliminatedIssueCount());
        Assert.assertEquals(3,resultProjectIsNull.getRemainingIssueCount());
    }


    @Test
    public void getStatisticalResults() {


    }



    /*
        等scan单元测试之后补充
     */
    @Test
    public void startMapping() {

    }
}