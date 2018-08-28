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

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    public Object getCommits(String project_id,Integer page,Integer size,Boolean is_whole) {
        String repo_id=restTemplate.getForObject(projectServicePath+"/repo-id?project-id="+project_id,String.class);
        JSONObject commitResponse=restTemplate.getForObject(commitServicePath+"?repo_id="+repo_id+"&page="+page+"&per_page="+size+"&is_whole=true",JSONObject.class);
        if(commitResponse!=null){
            List<String> scannedCommitId=scanDao.getScannedCommits(project_id);
            String lastScannedCommitId=scannedCommitId.get(scannedCommitId.size()-1);
            JSONArray commitArray=commitResponse.getJSONArray("data");
            int index=0;
            for(int i=0;i<commitArray.size();i++){
                JSONObject commit=commitArray.getJSONObject(i);
                String commit_id=commit.getString("commit_id");
                if(scannedCommitId.contains(commit_id)){
                    commit.put("isScanned",true);
                    if(commit_id.equals(lastScannedCommitId)){
                            index=i;
                    }
                }else{
                    commit.put("isScanned",false);
                }
            }
            Map<String,Object> result=new HashMap<>();
            if(is_whole){
                int totalCount=commitArray.size();
                result.put("totalCount",totalCount);
                if(totalCount>size){
                    result.put("commitList",commitArray.subList((page-1)*size,page*size>totalCount?totalCount:page*size));
                }else{
                    result.put("commitList",commitArray);
                }
            }else{
                List<Object> notScannedCommits=commitArray.subList(0,index);
                int totalCount=notScannedCommits.size();
                if(totalCount>size){
                    result.put("commitList",notScannedCommits.subList((page-1)*size,page*size>totalCount?totalCount:page*size));
                }else{
                    result.put("commitList",notScannedCommits);
                }
            }
            return result;
        }else {
            throw new RuntimeException("commit query failed!");
        }
    }

    @Override
    public Object getScannedCommits(String project_id) {
        return scanDao.getScannedCommits(project_id);
    }
}
