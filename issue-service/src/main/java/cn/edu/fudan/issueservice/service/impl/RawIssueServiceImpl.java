package cn.edu.fudan.issueservice.service.impl;

import cn.edu.fudan.issueservice.dao.LocationDao;
import cn.edu.fudan.issueservice.dao.RawIssueDao;
import cn.edu.fudan.issueservice.domain.Location;
import cn.edu.fudan.issueservice.domain.RawIssue;
import cn.edu.fudan.issueservice.service.RawIssueService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author WZY
 * @version 1.0
 **/
@Service
public class RawIssueServiceImpl implements RawIssueService {

    @Value("${commit.service.path}")
    private String commitServicePath;
    @Value("${code.service.path}")
    private String codeServicePath;
    @Value("${repoHome}")
    private String repoHome;

    @Value("${inner.service.path}")
    private String innerServicePath;

    private HttpHeaders httpHeaders;

    @Autowired
    public void setHttpHeaders(HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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
        rawIssueDao.insertRawIssueList(list);
        locationDao.insertLocationList(locations);
    }

    /**
     * 删除rawIssue以及对应的locations
     *
     * @param repoId 项目的id
     * @author WZY
     */
    @Transactional
    @Override
    public void deleteRawIssueByRepoId(String repoId) {
        locationDao.deleteLocationByRepoId(repoId);
        rawIssueDao.deleteRawIssueByRepoId(repoId);
    }

    @Override
    public void batchUpdateIssueId(List<RawIssue> list) {
        rawIssueDao.batchUpdateIssueId(list);
    }

    @Override
    public List<RawIssue> getRawIssueByCommitIDAndCategory(String commit_id,String category) {
        return rawIssueDao.getRawIssueByCommitIDAndCategory(commit_id,category);
    }

    @Override
    public List<RawIssue> getRawIssueByIssueId(String issueId) {
        return rawIssueDao.getRawIssueByIssueId(issueId);
    }

    @Override
    public Object getCode(String project_id, String commit_id, String file_path) {
        Map<String, Object> result = new HashMap<>();
        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
        String repo_id = restTemplate.exchange(innerServicePath + "/inner/project/repo-id?project-id=" + project_id, HttpMethod.GET, requestEntity, String.class).getBody();
        JSONObject response = restTemplate.getForObject(commitServicePath + "/checkout?repo_id=" + repo_id + "&commit_id=" + commit_id, JSONObject.class);
        if (response != null && response.getJSONObject("data").getString("status").equals("Successful")) {
            JSONObject codeResponse = restTemplate.getForObject(codeServicePath + "?file_path=" + repoHome + file_path, JSONObject.class);
            if (codeResponse != null && codeResponse.getJSONObject("data").getString("status").equals("Successful")) {
                result.put("code", codeResponse.getJSONObject("data").getString("content"));
            } else {
                throw new RuntimeException("load file failed!");
            }
        } else {
            throw new RuntimeException("check out failed!");
        }
        return result;
    }

    @Override
    public List<Location> getLocationsByRawIssueId(String raw_issue_id) {
        return locationDao.getLocations(raw_issue_id);
    }
}
