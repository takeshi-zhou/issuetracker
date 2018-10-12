package cn.edu.fudan.issueservice.service.impl;

import cn.edu.fudan.issueservice.dao.IssueDao;
import cn.edu.fudan.issueservice.dao.RawIssueDao;
import cn.edu.fudan.issueservice.domain.Issue;
import cn.edu.fudan.issueservice.domain.IssueCount;
import cn.edu.fudan.issueservice.domain.RawIssue;
import cn.edu.fudan.issueservice.service.IssueService;
import cn.edu.fudan.issueservice.util.LocationCompare;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @author WZY
 * @version 1.0
 **/
@Service
public class IssueServiceImpl implements IssueService {

    @Value("${solved.tag_id}")
    private String solvedTagId;
    @Value("${ignore.tag_id}")
    private String ignoreTagId;

    @Value("${commit.service.path}")
    private String commitServicePath;
    @Value("${tag.service.path}")
    private String tagServicePath;
    @Value("${inner.service.path}")
    private String innerServicePath;

    private HttpHeaders httpHeaders;

    @Autowired
    public void setHttpHeaders(HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
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

    private RawIssueDao rawIssueDao;

    @Autowired
    public void setRawIssueDao(RawIssueDao rawIssueDao) {
        this.rawIssueDao = rawIssueDao;
    }

    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void insertIssueList(List<Issue> list) {
        issueDao.insertIssueList(list);
    }

    @Override
    public void deleteIssueByRepoId(String repoId) {
        //先删除该项目所有issue对应的tag
        List<String> issueIds = issueDao.getIssueIdsByRepoId(repoId);
        if (issueIds != null && !issueIds.isEmpty()) {
            JSONObject response = restTemplate.postForObject(tagServicePath + "/tagged-delete", issueIds, JSONObject.class);
            if (response == null || response.getIntValue("code") != 200) {
                throw new RuntimeException("tag item delete failed!");
            }
        }
        issueDao.deleteIssueByRepoId(repoId);

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
            JSONArray tags = restTemplate.getForObject(tagServicePath + "?item_id=" + issue.getUuid(), JSONArray.class);
            issue.setTags(tags);
        }
    }

    @Override
    public Object getIssueList(String project_id, Integer page, Integer size,String category) {
        String repoId=getRepoId(project_id);
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("repo_id", repoId);
        param.put("size", size);
        param.put("category",category);
        //获取已经solve的和ignore的issue_ids
        List<String> tag_ids = new ArrayList<>();
        tag_ids.add(solvedTagId);
        tag_ids.add(ignoreTagId);
        JSONArray solved_issue_ids = restTemplate.postForObject(tagServicePath + "/item-ids", tag_ids, JSONArray.class);
        if (solved_issue_ids != null && solved_issue_ids.size() > 0) {
            param.put("solved_issue_ids", solved_issue_ids.toJavaList(String.class));
        }
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
        if (project_id == null || size == 0 || page == 0)
            throw new IllegalArgumentException("param lost!");
        String repoId = getRepoId(project_id);
        JSONArray tag_ids = requestParam.getJSONArray("tags");
        JSONArray types = requestParam.getJSONArray("types");
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> query = new HashMap<>();
        if(tag_ids!=null&&!tag_ids.isEmpty()){
            //只有在筛选条件都不为null的条件下才启用过滤
            JSONArray issue_ids = restTemplate.postForObject(tagServicePath + "/item-ids", tag_ids, JSONArray.class);
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

    private String getAccountId(String userToken) {
        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
        return restTemplate.exchange(innerServicePath + "/user/accountId?userToken=" + userToken, HttpMethod.GET, requestEntity, String.class).getBody();
    }

    private JSONArray getRepoIds(String account_id) {
        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
        return restTemplate.exchange(innerServicePath + "/inner/project/repo-ids?account_id=" + account_id, HttpMethod.GET, requestEntity, JSONArray.class).getBody();
    }

    private String getRepoId(String projectId) {
        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
        return restTemplate.exchange(innerServicePath + "/inner/project/repo-id?project-id=" + projectId, HttpMethod.GET, entity, String.class).getBody();
    }

    private IssueCount getOneRepoDashBoardInfo(String duration,String repoId,String category){
        Object newObject=stringRedisTemplate.opsForHash().get("dashboard:"+category+":"+ duration + ":" + repoId, "new");
        int newCount = newObject==null?0:Integer.parseInt((String)newObject);
        Object remainingObject=stringRedisTemplate.opsForHash().get("dashboard:"+category+":" + duration + ":" + repoId, "remaining");
        int remainingCount = remainingObject==null?0:Integer.parseInt((String)remainingObject);
        Object eliminatedObject=stringRedisTemplate.opsForHash().get("dashboard:"+category+":" + duration + ":" + repoId, "eliminated");
        int eliminatedCount = eliminatedObject==null?0:Integer.parseInt((String)eliminatedObject);
        return new IssueCount(newCount,eliminatedCount,remainingCount);
    }

    @Override
    public Object getDashBoardInfo(String duration, String project_id, String userToken,String category) {
        IssueCount result=new IssueCount(0,0,0);
        String account_id = getAccountId(userToken);
        if (project_id == null) {
            //未选择某一个project,显示该用户所有project的dashboard信息
            JSONArray repoIds = getRepoIds(account_id);
            if (repoIds != null&&!repoIds.isEmpty()) {
                for (int i = 0; i < repoIds.size(); i++) {
                    String currentRepoId = repoIds.getString(i);
                    result.issueCountUpdate(getOneRepoDashBoardInfo(duration,currentRepoId,category));
                }
            }
        } else {
            //只显示当前所选project的相关dashboard信息
            String currentRepoId = getRepoId(project_id);
            result.issueCountUpdate(getOneRepoDashBoardInfo(duration,currentRepoId,category));
        }
        return result;
    }

    private List<IssueCount> getFormatData(List<String> newList, List<String> remainingList, List<String> eliminatedList) {
        if (newList == null || remainingList == null || eliminatedList == null)
            return Collections.emptyList();
        List<IssueCount> list = new ArrayList<>();
        for (int i = 0; i < newList.size(); i++) {
            list.add(new IssueCount(Integer.parseInt(newList.get(i)), Integer.parseInt(remainingList.get(i)), Integer.parseInt(eliminatedList.get(i))));
        }
        return list;
    }

    @Override
    public Object getStatisticalResults(Integer month, String project_id, String userToken,String category) {
        Map<String, Object> result = new HashMap<>();
        String account_id = getAccountId(userToken);
        String newKey = "";
        String remainingKey = "";
        String eliminatedKey = "";
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
            String repoId = getRepoId(project_id);
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
        return issueDao.getExistIssueTypes(category);
    }

    private void addSolvedTag(String repo_id, String pre_commit_id) {
        List<String> issueIds = issueDao.getSolvedIssueIds(repo_id, pre_commit_id);
        if (issueIds != null && !issueIds.isEmpty()) {
            List<JSONObject> taggeds = new ArrayList<>();
            for (String issueId : issueIds) {
                JSONObject tagged = new JSONObject();
                tagged.put("item_id", issueId);
                tagged.put("tag_id", solvedTagId);
                taggeds.add(tagged);
            }
            restTemplate.postForObject(tagServicePath, taggeds, JSONObject.class);
        }
    }

    private void dashboardUpdate(String repo_id, int newIssueCount, int remainingIssueCount, int eliminatedIssueCount,String category) {
        //注意只有remaining是覆盖的，其余是累增的
        String todayKey = "dashboard:"+category+":day:" + repo_id;
        String weekKey = "dashboard:"+category+":week:" + repo_id;
        String monthKey = "dashboard:"+category+":month:" + repo_id;
        stringRedisTemplate.setEnableTransactionSupport(true);
        stringRedisTemplate.multi();
        stringRedisTemplate.opsForHash().increment(todayKey, "new", newIssueCount);
        stringRedisTemplate.opsForHash().put(todayKey, "remaining", String.valueOf(remainingIssueCount));
        stringRedisTemplate.opsForHash().increment(todayKey, "eliminated", eliminatedIssueCount);
        stringRedisTemplate.opsForHash().increment(weekKey, "new", newIssueCount);
        stringRedisTemplate.opsForHash().put(weekKey, "remaining", String.valueOf(remainingIssueCount));
        stringRedisTemplate.opsForHash().increment(weekKey, "eliminated", eliminatedIssueCount);
        stringRedisTemplate.opsForHash().increment(monthKey, "new", newIssueCount);
        stringRedisTemplate.opsForHash().put(monthKey, "remaining", String.valueOf(remainingIssueCount));
        stringRedisTemplate.opsForHash().increment(monthKey, "eliminated", eliminatedIssueCount);
        stringRedisTemplate.exec();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void startMapping(String repo_id, String pre_commit_id, String current_commit_id,String category) {
        List<Issue> insertIssueList = new ArrayList<>();
        if (pre_commit_id.equals(current_commit_id)) {
            //当前project第一次扫描，所有的rawIssue都是issue
            List<RawIssue> rawIssues = rawIssueDao.getRawIssueByCommitIDAndCategory(category,current_commit_id);
            if (rawIssues == null || rawIssues.isEmpty())
                return;
            for (RawIssue rawIssue : rawIssues) {
                String new_IssueId = UUID.randomUUID().toString();
                rawIssue.setIssue_id(new_IssueId);
                String targetFiles = rawIssue.getFile_name();
                Issue issue = new Issue(new_IssueId, rawIssue.getType(),category, current_commit_id, current_commit_id, rawIssue.getUuid(), rawIssue.getUuid(), repo_id, targetFiles);
                insertIssueList.add(issue);
            }
            int newIssueCount = insertIssueList.size();
            int remainingIssueCount = insertIssueList.size();
            int eliminatedIssueCount = 0;
            dashboardUpdate(repo_id, newIssueCount, remainingIssueCount, eliminatedIssueCount,category);
            rawIssueDao.batchUpdateIssueId(rawIssues);
        } else {
            //不是第一次扫描，需要和前一次的commit进行mapping
            List<RawIssue> rawIssues1 = rawIssueDao.getRawIssueByCommitIDAndCategory(category,pre_commit_id);
            List<RawIssue> rawIssues2 = rawIssueDao.getRawIssueByCommitIDAndCategory(category,current_commit_id);
            if (rawIssues2 == null || rawIssues1.isEmpty())
                return;
            //当前project已经扫描过的commit列表,是按commit_time从小到大排序的
            HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
            JSONArray commits = restTemplate.exchange(innerServicePath + "/inner/scan/commits?repo_id=" + repo_id+"&category="+category, HttpMethod.GET, requestEntity, JSONArray.class).getBody();
            Date start_commit_time = commits.getJSONObject(0).getDate("commit_time");
            Date end_commit_time = commits.getJSONObject(commits.size() - 1).getDate("commit_time");
            JSONObject jsonObject = restTemplate.getForObject(commitServicePath + "/commit-time?commit_id=" + current_commit_id, JSONObject.class);
            Date commit_time = jsonObject.getJSONObject("data").getDate("commit_time");
            //装需要更新的
            List<Issue> issues = new ArrayList<>();
            int equalsCount = 0;
            for (RawIssue issue_2 : rawIssues2) {
                boolean mapped = false;
                for (RawIssue issue_1 : rawIssues1) {
                    //如果issue_1已经匹配到一个issue_2,内部循环不再继续
                    if (LocationCompare.isUniqueIssue(issue_1, issue_2)) {
                        mapped = true;
                        equalsCount++;
                        String pre_issue_id = issue_1.getIssue_id();
                        //如果匹配到的上个commit的某个rawIssue已经有了issue_id,说明当前commit这个rawIssue也应该对应到这个issue
                        issue_2.setIssue_id(pre_issue_id);
                        Issue issue = issueDao.getIssueByID(pre_issue_id);
                        //更新当前issue的targetFiles
                        String currentTargetFiles = issue.getTarget_files();
                        if (!currentTargetFiles.contains(issue_2.getFile_name())) {
                            currentTargetFiles = currentTargetFiles + "," + issue_2.getFile_name();
                        }
                        if (!currentTargetFiles.contains(issue_1.getFile_name())) {
                            currentTargetFiles = currentTargetFiles + "," + issue_1.getFile_name();
                        }
                        issue.setTarget_files(currentTargetFiles);
                        if (commit_time.compareTo(start_commit_time) < 0) {
                            //如果当前扫的commit的时间比最先扫的commit还要提前，需要把当前issue的start更新
                            issue.setStart_commit(current_commit_id);
                            issue.setRaw_issue_start(issue_2.getUuid());
                            issues.add(issue);
                        } else if (commit_time.compareTo(end_commit_time) > 0) {
                            //如果当前扫的commit的时间比最后扫的commit还要往后，需要把当前issue的end更新
                            issue.setEnd_commit(current_commit_id);
                            issue.setRaw_issue_end(issue_2.getUuid());
                            issues.add(issue);
                        }
                        break;
                    }
                }
                if (!mapped) {
                    //如果当前commit的某个rawIssue没有在上个commit的rawissue列表里面找到匹配，将它作为新的issue插入
                    String new_IssueId = UUID.randomUUID().toString();
                    issue_2.setIssue_id(new_IssueId);
                    String targetFiles = issue_2.getFile_name();
                    insertIssueList.add(new Issue(new_IssueId, issue_2.getType(),category, current_commit_id, current_commit_id, issue_2.getUuid(), issue_2.getUuid(), repo_id, targetFiles));
                }
            }
            if (!issues.isEmpty()) {
                //更新issue
                issueDao.batchUpdateIssue(issues);
            }
            int eliminatedIssueCount = rawIssues1.size() - equalsCount;
            int remainingIssueCount = rawIssues2.size();
            int newIssueCount = rawIssues2.size() - equalsCount;
            dashboardUpdate(repo_id, newIssueCount, remainingIssueCount, eliminatedIssueCount,category);
            rawIssueDao.batchUpdateIssueId(rawIssues2);
        }
        //新的issue
        if (!insertIssueList.isEmpty()) {
            issueDao.insertIssueList(insertIssueList);
        }
        if (!pre_commit_id.equals(current_commit_id)) {
            addSolvedTag(repo_id, pre_commit_id);
        }
    }
}
