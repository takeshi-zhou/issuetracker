package cn.edu.fudan.projectmanager.service.impl;



import cn.edu.fudan.projectmanager.dao.*;
import cn.edu.fudan.projectmanager.domain.NeedDownload;
import cn.edu.fudan.projectmanager.domain.Project;
import cn.edu.fudan.projectmanager.service.ProjectService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.UUID;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Value("${account.service.path}")
    private String accountServicePath;
    @Value("${issue.service.path}")
    private String issueServicePath;
    @Value("${scan.service.path}")
    private String scanServicePath;

    private KafkaTemplate kafkaTemplate;

    @Autowired
    public void setKafkaTemplate(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private ProjectDao projectDao;

    @Autowired
    public void setProjectDao(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    @SuppressWarnings("unchecked")
    private void send(String projectId, String url){
        NeedDownload needDownload=new NeedDownload(projectId,url);
        kafkaTemplate.send("ProjectManager", JSONObject.toJSONString(needDownload));
    }

    @Override
    public void addOneProject(String userToken,String url) {
        String account_id=restTemplate.getForObject(accountServicePath+"/accountId?userToken="+userToken,String.class);
        String projectName=url.substring(url.lastIndexOf("/")+1);
        String projectId= UUID.randomUUID().toString();
        Project project=new Project();
        project.setUuid(projectId);
        project.setName(projectName);
        project.setUrl(url);
        project.setAccount_id(account_id);
        project.setDownload_status("Downloading");
        project.setScan_status("Not Scanned");
        projectDao.addOneProject(project);
        //向RepoManager这个Topic发送消息，请求开始下载
        send(projectId,url);
    }

    @Override
    public Object getProjectList(String userToken) {
        RestTemplate restTemplate=new RestTemplate();
        String account_id=restTemplate.getForObject(accountServicePath+"/accountId?userToken="+userToken,String.class);
        return projectDao.getProjectByAccountId(account_id);
    }

    @Override
    public Project getProjectByID(String projectId) {
        return projectDao.getProjectByID(projectId);
    }

    @Override
    public void updateProjectStatus(Project project) {
        projectDao.updateProjectStatus(project);
    }


    @Override
	public void remove(String projectId) {
        restTemplate.delete(issueServicePath+"/RawIssue/delete/"+projectId);
        restTemplate.delete(issueServicePath+"/Issue/delete/"+projectId);
        restTemplate.delete(scanServicePath+"/delete/"+projectId);
		projectDao.remove(projectId);
	}

    @Override
    public String getProjectNameById(String projectId) {
        return projectDao.getProjectNameById(projectId);
    }

    @Override
    public String getRepoPath(String project_id) {
        return projectDao.getRepoPath(project_id);
    }
}
