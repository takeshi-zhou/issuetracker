package cn.edu.fudan.projectmanager.service.impl;



import cn.edu.fudan.projectmanager.dao.*;
import cn.edu.fudan.projectmanager.domain.NeedDownload;
import cn.edu.fudan.projectmanager.domain.Project;
import cn.edu.fudan.projectmanager.service.ProjectService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Value("${github.api.path}")
    private String githubAPIPath;
    @Value("${inner.service.path}")
    private String innerServicePath;
    @Value("${inner.header.key}")
    private  String headerKey;
    @Value("${inner.header.value}")
    private  String headerValue;

    private KafkaTemplate kafkaTemplate;

    private HttpHeaders headers;

    @Autowired
    public void setKafkaTemplate(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ProjectServiceImpl(){
        //构造函数中初始化kong的header
        headers = new HttpHeaders();
        headers.add(headerKey,headerValue);
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

    private String getAccountId(String userToken){
        HttpEntity<String> requestEntity=new HttpEntity<>(null,headers);
        return restTemplate.exchange(innerServicePath+"/user/accountId?userToken="+userToken,HttpMethod.GET,requestEntity,String.class).getBody();
    }

    private void checkProjectURL(String url){
        Pattern pattern=Pattern.compile("https://github.com(/[\\w-]+/[\\w-]+)");
        Matcher matcher=pattern.matcher(url);
        if(!matcher.matches()){
            throw new RuntimeException("invalid url!");
        }
        //前面做过一次全匹配，需要把matcher重置一下才能接着从头匹配
        matcher.reset();
        boolean isMavenProject=false;
        while(matcher.find()){
            String author_projectName=matcher.group(1);
            JSONArray fileList=restTemplate.getForEntity(githubAPIPath+author_projectName+"/contents",JSONArray.class).getBody();
            if(fileList!=null){
                for(int i=0;i<fileList.size();i++){
                   JSONObject file=fileList.getJSONObject(i);
                   if(file.getString("name").equals("pom.xml")){
                       isMavenProject=true;
                       break;
                   }
                }
                if(!isMavenProject)
                    throw new RuntimeException("failed,this project is not maven project!");
            }else{
                throw new RuntimeException("invalid url!");
            }
        }
    }

    @Override
    public void addOneProject(String userToken,String url) {
        if(url==null){
            throw new RuntimeException("please input the project url!");
        }
        url=url.trim();
        checkProjectURL(url);
        String account_id=getAccountId(userToken);
        if(projectDao.hasBeenAdded(account_id,url)){
            throw new RuntimeException("The project has been added!");
        }
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
        String account_id=getAccountId(userToken);
        return projectDao.getProjectByAccountId(account_id);
    }

    @Override
    public Object getProjectIdList(String account_id) {
        return projectDao.getProjectByAccountId(account_id).stream().map(Project::getUuid).collect(Collectors.toList());
    }

    @Override
    public Object getProjectListByKeyWord(String userToken, String keyWord) {
        String account_id=getAccountId(userToken);
        return projectDao.getProjectByKeyWordAndAccountId(account_id,keyWord.trim());
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
        HttpEntity<String> requestEntity=new HttpEntity<>(null,headers);
        restTemplate.exchange(innerServicePath+"/inner/issue/"+projectId, HttpMethod.DELETE,requestEntity,JSONObject.class);
        restTemplate.exchange(innerServicePath+"/inner/raw-issue/"+projectId,HttpMethod.DELETE,requestEntity,JSONObject.class);
        restTemplate.exchange(innerServicePath+"/inner/scan/"+projectId,HttpMethod.DELETE,requestEntity,JSONObject.class);
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

    @Override
    public String getRepoId(String projectId) {
        return projectDao.getRepoId(projectId);
    }
}
