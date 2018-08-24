package cn.edu.fudan.scanservice.service.impl;


import cn.edu.fudan.scanservice.dao.ScanDao;
import cn.edu.fudan.scanservice.domain.Scan;
import cn.edu.fudan.scanservice.service.ScanService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
public class ScanServiceImpl implements ScanService {

    @Value("${project.service.path}")
    private String projectServicePath;

    @Value("${commit.service.path}")
    private String commitServicePath;

    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    private ScanDao scanDao;

    @Autowired
    public void setScanDao(ScanDao scanDao) {
        this.scanDao = scanDao;
    }

    @Override
    public void insertOneScan(Scan scan) {
        scanDao.insertOneScan(scan);
    }

    @Override
    public void deleteScanByProjectId(String projectId) {
        scanDao.deleteScanByProjectId(projectId);
    }

    @Override
    public void updateOneScan(Scan scan) {
        scanDao.updateOneScan(scan);
    }

    @Override
    public String getLatestScannedCommitId(String project_id) {
        return scanDao.getLatestScannedCommitId(project_id);
    }

    @Override
    public Object getTillCommitDateByProjectId(String projectId) {
        return scanDao.getTillCommitDateByProjectId(projectId);
    }

    @Override
    public JSONArray getCommits(String project_id,String option) {
        String repoId = restTemplate.getForObject(projectServicePath+"/"+project_id,Object.class).toString();
        // if has option all：list all commits
        if(option.equals("all"))
            return restTemplate.getForObject(commitServicePath+"?repo-id="+repoId, JSONObject.class).getJSONArray("data");
        else{
            JSONArray jsonArray = restTemplate.getForObject(commitServicePath+"?repo-id="+repoId, JSONObject.class).getJSONArray("data");
            List<String> list = scanDao.getScannedCommits(project_id) ;
            for (int i = 0 ; i<jsonArray.size();i++){
                if (list.contains(jsonArray.getJSONObject(i).getString("uuid")))
                    jsonArray.remove(i);
            }
            return jsonArray;
        }
    }
}
