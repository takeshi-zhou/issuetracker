package cn.edu.fudan.scanscheduler.service.impl;

import cn.edu.fudan.scanscheduler.domain.Scan;
import cn.edu.fudan.scanscheduler.domain.ScanInitialInfo;
import cn.edu.fudan.scanscheduler.domain.ScanResult;
import cn.edu.fudan.scanscheduler.service.ScanOperation;
import cn.edu.fudan.scanscheduler.util.DateTimeUtil;
import cn.edu.fudan.scanscheduler.util.ExcuteShellUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.UUID;


@Service
public class ScanOperationAdapter implements ScanOperation {

    @Value("${commit.service.path}")
    private String commitServicePath;
    @Value("${project.service.path}")
    private String projectServicePath;
    @Value("${issue.service.path}")
    private String issueServicePath;
    @Value("${scan.service.path}")
    private String scanServicePath;

    protected RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Override
    public  boolean isScanned(String commitId){
        return restTemplate.getForObject(commitServicePath+"/isScanned/"+commitId,Integer.class)==1;
    }

    @Override
    public boolean checkOut(String projectId, String commitId){
        String repoPath=restTemplate.getForObject(projectServicePath+"/repoPath/"+projectId,String.class);
        return ExcuteShellUtil.executeCheckout(repoPath,commitId);
    }

    @Override
    public ScanInitialInfo initialScan(String projectId, String commitId) {
        Date startTime=new Date();
        String repoPath=restTemplate.getForObject(projectServicePath+"/repoPath/"+projectId,String.class);
        JSONObject currentProject=restTemplate.getForObject(projectServicePath+"/project/"+projectId,JSONObject.class);
        String projectName=currentProject.getString("name");
        String repoId=currentProject.getString("repo_id");
        //新建一个Scan对象
        Scan scan=new Scan();
        scan.setName(projectName+"-"+startTime.getTime());
        scan.setStart_time(DateTimeUtil.formatedDate(startTime));
        scan.setStatus("doing...");
        scan.setProject_id(projectId);
        scan.setCommit_id(commitId);
        String uuid= UUID.randomUUID().toString();
        scan.setUuid(uuid);
        return new ScanInitialInfo(scan,projectName,repoId, repoPath);
    }

    @Override
    public ScanResult doScan(ScanInitialInfo scanInitialInfo){
        //等待子类的具体实现
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean mapping(String projectId,String commitId) {
        String pre_commit_id=restTemplate.getForObject(scanServicePath+"/lastScannedCommit/"+projectId,String.class);
        JSONObject requestParam=new JSONObject();
        requestParam.put("project_id",projectId);
        if(pre_commit_id!=null)
            requestParam.put("pre_commit_id",pre_commit_id);
        else
            requestParam.put("pre_commit_id",commitId);
        requestParam.put("current_commit_id",commitId);
        JSONObject result=restTemplate.postForEntity(issueServicePath+"/Issue/mapping",requestParam,JSONObject.class).getBody();
        return result.getIntValue("code")==200;
    }

    @Override
    public boolean updateScan(ScanInitialInfo scanInitialInfo) {
        Scan scan=scanInitialInfo.getScan();
        String repoId=scanInitialInfo.getRepoId();
        //更新till_commit_time
        String commit_time=restTemplate.getForObject(commitServicePath+"/commitDate/"+scan.getCommit_id(),Object.class).toString();
        String till_commit=null;
        Object till_commit_object=restTemplate.getForObject(commitServicePath+"/tillCommitDate/"+repoId,Object.class);
        if(till_commit_object!=null){
            till_commit=till_commit_object.toString();
        }
        if(till_commit==null||commit_time.compareTo(till_commit)>0){
            JSONObject requestParam =new JSONObject();
            requestParam.put("uuid",scan.getProject_id());
            requestParam.put("till_commit_time",commit_time);
            restTemplate.postForEntity(projectServicePath+"/update",requestParam,JSONObject.class);
        }

        //更新当前Scan的状态
        scan.setStatus("done");//设为结束状态
        scan.setEnd_time(new Date());
        JSONObject param=new JSONObject();
        param.put("uuid",scan.getUuid());
        param.put("name",scan.getName());
        param.put("start_time",DateTimeUtil.format(scan.getStart_time()));
        param.put("end_time",DateTimeUtil.format(scan.getEnd_time()));
        param.put("status",scan.getStatus());
        param.put("result_summary",scan.getResult_summary());
        param.put("project_id",scan.getUuid());
        param.put("commit_id",scan.getCommit_id());
        JSONObject result=restTemplate.postForEntity(scanServicePath+"/add",param,JSONObject.class).getBody();
        //restTemplate.postForEntity(scanServicePath+"/update",scan,JSONObject.class).getBody();
        return result.getIntValue("code")==200;
    }
}
