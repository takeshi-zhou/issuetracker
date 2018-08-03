package cn.edu.fudan.scanservice.service.impl;

import cn.edu.fudan.scanservice.domain.ScanResult;
import cn.edu.fudan.scanservice.service.KafkaService;
import cn.edu.fudan.scanservice.util.DateTimeUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

/**
 * @author WZY
 * @version 1.0
 **/
@Service
public class KafkaServiceImpl implements KafkaService{

    private Logger logger= LoggerFactory.getLogger(ScanServiceImpl.class);

    @Value("${project.service.path}")
    private String projectServicePath;

    @Value("${commit.service.path}")
    private String commitServicePath;

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


    private void updateProject(JSONObject projectParam){
        JSONObject json = restTemplate.postForEntity(projectServicePath+"/update", projectParam, JSONObject.class).getBody();
        if(json.getIntValue("code")!=200){
            throw new RuntimeException("project status initial failed!");
        }
    }

    private void updateCommit(JSONObject commitParam){
        JSONObject json = restTemplate.postForEntity(commitServicePath+"/update", commitParam, JSONObject.class).getBody();
        if(json.getIntValue("code")!=200){
            throw new RuntimeException("project status initial failed!");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void sendScanMessage(JSONObject requestParam) {
        String projectId=requestParam.getString("projectId");
        JSONObject postData = new JSONObject();
        postData.put("uuid", projectId);
        postData.put("scan_status","Scanning");
        postData.put("last_scan_time", DateTimeUtil.format(new Date()));
        updateProject(postData);
        kafkaTemplate.send("Scan",requestParam.toJSONString());
        logger.info("send message to topic -> Scan " +requestParam.toJSONString());
    }

    @Override
    @KafkaListener(id="scanResult",topics = {"ScanResult"},groupId = "scanResult")
    public void updateCommitScanStatus(ConsumerRecord<String, String> consumerRecord) {
        String msg=consumerRecord.value();
        logger.info("received message from topic -> "+consumerRecord.topic()+" : "+msg);
        ScanResult scanResult=JSONObject.parseObject(msg,ScanResult.class);
        String projectId=scanResult.getProjectId();
        JSONObject projectParam=new JSONObject();
        projectParam.put("uuid",projectId);
        if(scanResult.getStatus().equals("success")){
            JSONObject commitParam=new JSONObject();
            commitParam.put("is_scanned",1);
            commitParam.put("commit_id",scanResult.getCommitId());
            updateCommit(commitParam);

            projectParam.put("scan_status","Scanned");
        }else{
            projectParam.put("scan_status",scanResult.getDescription());
        }
        updateProject(projectParam);
    }
}
