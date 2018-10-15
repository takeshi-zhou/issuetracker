package cn.edu.fudan.scanservice.service.impl;


import cn.edu.fudan.scanservice.dao.ScanDao;
import cn.edu.fudan.scanservice.domain.Scan;
import cn.edu.fudan.scanservice.service.ScanService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ScanServiceImpl implements ScanService {

    @Value("${inner.service.path}")
    private String innerServicePath;
    @Value("${commit.service.path}")
    private String commitServicePath;

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
    public void deleteScanByRepoId(String repoId) {
        scanDao.deleteScanByRepoId(repoId);
    }

    @Override
    public void updateOneScan(Scan scan) {
        scanDao.updateOneScan(scan);
    }

    @Override
    public Object getCommits(String project_id, Integer page, Integer size, Boolean is_whole,String category) {
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
        String repo_id = restTemplate.exchange(innerServicePath + "/inner/project/repo-id?project-id=" + project_id, HttpMethod.GET, httpEntity, String.class).getBody();
        JSONObject commitResponse = restTemplate.getForObject(commitServicePath + "?repo_id=" + repo_id + "&page=" + page + "&per_page=" + size + "&is_whole=true", JSONObject.class);
        if (commitResponse != null) {
            List<String> scannedCommitId = scanDao.getScannedCommits(repo_id,category);
            JSONArray commitArray = commitResponse.getJSONArray("data");
            int index = 0;
            if (scannedCommitId.isEmpty()) {
                //全都没扫过
                for (int i = 0; i < commitArray.size(); i++) {
                    JSONObject commit = commitArray.getJSONObject(i);
                    commit.put("isScanned", false);
                }
                is_whole = true;
            } else {
                String lastScannedCommitId = scannedCommitId.get(scannedCommitId.size() - 1);
                //add for disable commits befor scanning commits
                boolean disabled = false;
                for (int i = 0; i < commitArray.size(); i++) {
                    JSONObject commit = commitArray.getJSONObject(i);
                    String commit_id = commit.getString("commit_id");
                    if (scannedCommitId.contains(commit_id) || disabled) {
                        commit.put("isScanned", true);
                        if (commit_id.equals(lastScannedCommitId)) {
                            index = i;
                        }
                        disabled = true;
                    } else {
                        commit.put("isScanned", false);
                    }
                    /*
                    if(scannedCommitId.contains(commit_id)){
                        commit.put("isScanned",true);
                        if(commit_id.equals(lastScannedCommitId)){
                            index=i;
                        }
                    }else{
                        commit.put("isScanned",false);
                    }*/
                }
            }
            Map<String, Object> result = new HashMap<>();
            if (is_whole) {
                int totalCount = commitArray.size();
                result.put("totalCount", totalCount);
                if (totalCount > size) {
                    result.put("commitList", commitArray.subList((page - 1) * size, page * size > totalCount ? totalCount : page * size));
                } else {
                    result.put("commitList", commitArray);
                }
            } else {
                List<Object> notScannedCommits = commitArray.subList(0, index);
                int totalCount = notScannedCommits.size();
                result.put("totalCount", totalCount);
                if (totalCount > size) {
                    result.put("commitList", notScannedCommits.subList((page - 1) * size, page * size > totalCount ? totalCount : page * size));
                } else {
                    result.put("commitList", notScannedCommits);
                }
            }
            return result;
        } else {
            throw new RuntimeException("commit query failed!");
        }
    }

    @Override
    public Object getScannedCommits(String repoId,String category) {
        return scanDao.getScans(repoId,category);
    }

    @Override
    public String getLatestScannedCommitId(String repo_id, String category) {

        return scanDao.getLatestScannedCommitId(repo_id, category);
    }
}
