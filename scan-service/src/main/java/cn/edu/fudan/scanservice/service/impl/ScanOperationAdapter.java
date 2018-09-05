package cn.edu.fudan.scanservice.service.impl;

import cn.edu.fudan.scanservice.dao.ScanDao;
import cn.edu.fudan.scanservice.domain.Scan;
import cn.edu.fudan.scanservice.domain.ScanInitialInfo;
import cn.edu.fudan.scanservice.domain.ScanResult;
import cn.edu.fudan.scanservice.service.ScanOperation;
import cn.edu.fudan.scanservice.util.DateTimeUtil;
import cn.edu.fudan.scanservice.util.ExcuteShellUtil;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
public class ScanOperationAdapter implements ScanOperation {

    private final static Logger logger= LoggerFactory.getLogger(ScanOperationAdapter.class);

    @Value("${commit.service.path}")
    private String commitServicePath;

    @Value("${inner.service.path}")
    private String innerServicePath;
    @Value("${inner.header.key}")
    private  String headerKey;
    @Value("${inner.header.value}")
    private  String headerValue;

    private HttpHeaders headers;
    private void initHeaders(){
        if(headers!=null)
            return;
        headers = new HttpHeaders();
        headers.add(headerKey,headerValue);
    }


    protected RestTemplate restTemplate;

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
    public  boolean isScanned(String commitId){
        return scanDao.isScanned(commitId);
    }

    @Override
    public boolean checkOut(String project_id, String commit_id){
        initHeaders();
        HttpEntity<String> entity=new HttpEntity<>(headers);
        String repo_id=restTemplate.exchange(innerServicePath+"/inner/project/repo-id?project-id="+project_id, HttpMethod.GET,entity,String.class).getBody();
        JSONObject response=restTemplate.getForObject(commitServicePath+"/checkout?repo_id="+repo_id+"&commit_id="+commit_id, JSONObject.class);
        return response!=null&&response.getJSONObject("data").getString("status").equals("Successful");
    }

    @Override
    public ScanInitialInfo initialScan(String projectId, String commitId)  {
        Date startTime=new Date();
        initHeaders();
        HttpEntity<String> entity=new HttpEntity<>(headers);
        String repoPath=restTemplate.exchange(innerServicePath+"/inner/project/repo-path/"+projectId,HttpMethod.GET,entity,String.class).getBody();
        JSONObject currentProject=restTemplate.exchange(innerServicePath+"/inner/project"+projectId,HttpMethod.GET,entity,JSONObject.class).getBody();
        String projectName=currentProject.getString("name");
        String repoId=currentProject.getString("repo_id");
        //新建一个Scan对象
        Scan scan=new Scan();
        scan.setName(projectName+"-"+startTime.getTime());
        scan.setStart_time(DateTimeUtil.formatedDate(startTime));
        scan.setStatus("doing...");
        scan.setProject_id(projectId);
        scan.setCommit_id(commitId);
        //scan.set
        String uuid= UUID.randomUUID().toString();
        scan.setUuid(uuid);
        //use api provided by commit-service
        JSONObject jsonObject = restTemplate.getForObject(commitServicePath+"/commit-time?commit_id="+commitId,JSONObject.class);
        Date commit_time =jsonObject.getJSONObject("data").getDate("commit_time");
        scan.setCommit_time(DateTimeUtil.formatedDate(commit_time));
        return new ScanInitialInfo(scan,projectName,repoId, repoPath);
    }

    @Override
    public ScanResult doScan(ScanInitialInfo scanInitialInfo){
        //等待子类的具体实现
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean mapping(String projectId,String commitId) {
        String pre_commit_id=scanDao.getLatestScannedCommitId(projectId);
        JSONObject requestParam=new JSONObject();
        requestParam.put("project_id",projectId);
        if(pre_commit_id!=null)
            requestParam.put("pre_commit_id",pre_commit_id);
        else
            requestParam.put("pre_commit_id",commitId);
        requestParam.put("current_commit_id",commitId);
        logger.info("mapping between "+requestParam.toJSONString());
        initHeaders();
        HttpEntity<Object> entity=new HttpEntity<>(requestParam,headers);
        JSONObject result=restTemplate.exchange(innerServicePath+"/inner/issue/mapping",HttpMethod.POST,entity,JSONObject.class).getBody();
        return result!=null&&result.getIntValue("code")==200;
    }

    @Override
    public boolean updateScan(ScanInitialInfo scanInitialInfo) {
        Scan scan=scanInitialInfo.getScan();
        //String repoId=scanInitialInfo.getRepoId();
        //更新project 表 的till_commit_time
        String commit_time=scan.getCommit_time().toString();
        String till_commit=null;
        //Object till_commit_object=restTemplate.getForObject(commitServicePath+"/tillCommitDate/"+repoId,Object.class);
        Object till_commit_object = scanDao.getTillCommitDateByProjectId(scan.getProject_id());
        if(till_commit_object!=null){
            till_commit=till_commit_object.toString();
        }
        //如果当前commit是当前项目扫描的第一个commit或者当前commit的时间在till_commit时间之后，需要更新till_commit
        if(till_commit==null||commit_time.compareTo(till_commit)>0){
            JSONObject requestParam =new JSONObject();
            requestParam.put("uuid",scan.getProject_id());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = formatter.format(scan.getCommit_time());
            requestParam.put("till_commit_time",dateString);
            initHeaders();
            HttpEntity<Object> entity=new HttpEntity<>(requestParam,headers);
            restTemplate.exchange(innerServicePath+"/inner/project",HttpMethod.PUT,entity,JSONObject.class);
        }

        //更新当前Scan的状态
        scan.setStatus("done");//设为结束状态
        scan.setEnd_time(new Date());
        scanDao.insertOneScan(scan);
        return true;
    }
}
