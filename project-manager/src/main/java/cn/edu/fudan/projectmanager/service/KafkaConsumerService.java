package cn.edu.fudan.projectmanager.service;

import cn.edu.fudan.projectmanager.dao.ProjectDao;
import cn.edu.fudan.projectmanager.domain.CompleteDownLoad;
import cn.edu.fudan.projectmanager.domain.Project;
import cn.edu.fudan.projectmanager.domain.RepoBasicInfo;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class KafkaConsumerService {


    private ProjectService projectService;

    private ProjectDao projectDao;

    @Autowired
    public void setProjectDao(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    @Autowired
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    private Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @KafkaListener(id = "projectUpdate", topics = {"RepoManager"}, groupId = "project")
    public void update(ConsumerRecord<String, String> consumerRecord) {
        String msg = consumerRecord.value();
        logger.info("receive message from topic -> " + consumerRecord.topic() + " : " + msg);
        CompleteDownLoad completeDownLoad = JSONObject.parseObject(msg, CompleteDownLoad.class);
        Project currentProject = projectService.getProjectByID(completeDownLoad.getProjectId());
        currentProject.setLanguage(completeDownLoad.getLanguage());
        currentProject.setDownload_status(completeDownLoad.getStatus());
        currentProject.setDescription(completeDownLoad.getDescription());
        currentProject.setRepo_id(completeDownLoad.getRepo_id());
        projectService.updateProjectStatus(currentProject);
    }

    @KafkaListener(id = "repoIn", topics = {"repoIn"}, groupId = "repoIn")
    public void repoIn(ConsumerRecord<String, String> consumerRecord) throws ParseException {
        String msg = consumerRecord.value();
        logger.info("Auto-Scan  -> topic :" + consumerRecord.topic() + ";  msg :" + msg);
        RepoBasicInfo repoBasicInfo = JSONObject.parseObject(msg, RepoBasicInfo.class);
        projectDao.addOneProject(Project.createOneProjectByRepoBasicInfo(repoBasicInfo, "bug"));
        projectDao.addOneProject(Project.createOneProjectByRepoBasicInfo(repoBasicInfo, "clone"));
    }
}
