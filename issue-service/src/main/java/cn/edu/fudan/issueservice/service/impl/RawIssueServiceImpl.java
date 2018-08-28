package cn.edu.fudan.issueservice.service.impl;

import cn.edu.fudan.issueservice.dao.LocationDao;
import cn.edu.fudan.issueservice.dao.RawIssueDao;
import cn.edu.fudan.issueservice.domain.Location;
import cn.edu.fudan.issueservice.domain.RawIssue;
import cn.edu.fudan.issueservice.service.RawIssueService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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


    @Value("${project.service.path}")
    private String projectServicePath;

    @Value("${commit.service.path}")
    private String commitServicePath;

    @Value("${code.service.path}")
    private String codeServicePath;

    @Value("${repoHome}")
    private String repoHome;

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
     * @author WZY
     * @param list rawIssue的列表，里面包含了locations
     */
    @Override
    public void insertRawIssueList(List<RawIssue> list) {
        List<Location> locations=new ArrayList<>();
        for(RawIssue rawIssue:list){
            locations.addAll(rawIssue.getLocations());
        }
        rawIssueDao.insertRawIssueList(list);
        locationDao.insertLocationList(locations);
    }

    /**
     * 删除rawIssue以及对应的locations
     * @author WZY
     * @param projectId 项目的id
     */
    @Override
    public void deleteRawIssueByProjectId(String projectId) {
        locationDao.deleteLocationByProjectId(projectId);
        rawIssueDao.deleteRawIssueByProjectId(projectId);
    }

    @Override
    public void batchUpdateIssueId(List<RawIssue> list) {
        rawIssueDao.batchUpdateIssueId(list);
    }

    @Override
    public List<RawIssue> getRawIssueByCommitID(String commit_id) {
        return rawIssueDao.getRawIssueByCommitID(commit_id);
    }

    @Override
    public List<RawIssue> getRawIssueByIssueId(String issueId) {
        return rawIssueDao.getRawIssueByIssueId(issueId);
    }

    @Override
    public Object getCode(String project_id, String commit_id, String file_path) {
        Map<String,Object> result=new HashMap<>();
        String repo_id=restTemplate.getForObject(projectServicePath+"/repo-id?project-id="+project_id,String.class);
        JSONObject response=restTemplate.getForObject(commitServicePath+"/checkout?repo_id="+repo_id+"&commit_id="+commit_id, JSONObject.class);
        if(response!=null&&response.getJSONObject("data").getString("status").equals("Successful")){
            JSONObject codeResponse=restTemplate.getForObject(codeServicePath+"?file_path="+repoHome+file_path,JSONObject.class);
            if(codeResponse!=null&&codeResponse.getJSONObject("data").getString("status").equals("Successful")){
                result.put("code",codeResponse.getJSONObject("data").getString("content"));
            }else{
                throw  new RuntimeException("load file failed!");
            }
        }else{
            throw  new RuntimeException("check out failed!");
        }
        return result;
    }

    @Override
    public List<Location> getLocationsByRawIssueId(String raw_issue_id) {
        return locationDao.getLocations(raw_issue_id);
    }
}
