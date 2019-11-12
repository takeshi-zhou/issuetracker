package cn.edu.fudan.projectmanager.dao;

import cn.edu.fudan.projectmanager.ProjectManagerApplication;
import cn.edu.fudan.projectmanager.ProjectManagerApplicationTests;
import cn.edu.fudan.projectmanager.domain.Project;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;

public class ProjectDaoTest extends ProjectManagerApplicationTests {

    private RestTemplate restTemplate;


    private ProjectDao projectDao;

    @Autowired
    public void dao(ProjectDao projectDao){
        this.projectDao=projectDao;
    }

    @Autowired
    public void insert(RestTemplate restTemplate) {
        this.restTemplate=restTemplate;
    }

//    @Test
//    public void addOneProject() {
//        ResponseEntity<String> resp = restTemplate.getForEntity("https://api.github.com/repos/qiurunze123/miaosha", String.class);
//        JSONObject json = JSONObject.parseObject(resp.getBody());
//        Project project = new Project();
//        project.setUuid("pro1");
//        project.setName("Java");
//        project.setLanguage("Java");
//        project.setType("bug");
//        project.setUrl("https://github.com/qiurunze123/miaosha");
//        project.setVcs_type("git");
//        project.setAccount_id("1");
//        project.setBranch("master");
//        project.setDownload_status("Downloaded");
//        project.setScan_status("Not Scanned");
//        project.setDescription(json.getString("description"));
//        project.setRepo_id("227a91de-a522-11e8-8fa0-d067e5ea858d");
//        projectDao.addOneProject(project);
//    }
}