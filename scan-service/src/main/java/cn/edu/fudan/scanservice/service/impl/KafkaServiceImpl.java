package cn.edu.fudan.scanservice.service.impl;


import cn.edu.fudan.scanservice.domain.ScanResult;
import cn.edu.fudan.scanservice.service.KafkaService;
import cn.edu.fudan.scanservice.task.ScanTask;
import cn.edu.fudan.scanservice.util.DateTimeUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author WZY
 * @version 1.0
 **/
@Service
public class KafkaServiceImpl implements KafkaService{

    private Logger logger= LoggerFactory.getLogger(ScanServiceImpl.class);

    @Value("${project.service.path}")
    private String projectServicePath;

    @Value("${rawIssue.service.path}")
    private String rawIssueServicePath;

    private ScanTask scanTask;

    @Autowired
    public void setScanTask(ScanTask scanTask) {
        this.scanTask = scanTask;
    }

    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    //初始化project的一些状态,表示目前正在scan
    private void initialProject(String projectId){
        JSONObject postData = new JSONObject();
        postData.put("uuid", projectId);
        postData.put("scan_status","Scanning");
        postData.put("last_scan_time", DateTimeUtil.format(new Date()));
        updateProject(postData);
    }

    @SuppressWarnings("unchecked")
    private void updateProject(JSONObject projectParam){
        try{
            restTemplate.put(projectServicePath,projectParam);
        }catch (Exception e){
            throw new RuntimeException("project status initial failed!");
        }
    }

    /**
     * 通过前台请求触发scan操作
     * @author WZY
     */
    @SuppressWarnings("unchecked")
    @Override
    public void scanByRequest(JSONObject requestParam) {
        String projectId=requestParam.getString("projectId");
        String commitId=requestParam.getString("commitId");
        initialProject(projectId);
        Future<String> future=scanTask.run(projectId,commitId);
        //开一个工作者线程来管理异步任务的超时
        new Thread(()->{
            try{
                future.get(15, TimeUnit.MINUTES);//设置15分钟的超时时间
            }catch (TimeoutException e){
                //因scan超时而抛出异常
                logger.error("超时了");
                future.cancel(false);
                JSONObject projectParam=new JSONObject();
                projectParam.put("uuid",projectId);
                projectParam.put("scan_status","Scan Time Out");
                updateProject(projectParam);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 监听消息队列中project的scan消息触发scan操作,暂时没有用到
     * @author WZY
     */
    @Override
    @KafkaListener(id="projectScan",topics = {"Scan"},groupId = "scan")
    public void scanByMQ(ConsumerRecord<String, String> consumerRecord) {
        String msg=consumerRecord.value();
        logger.info("received message from topic -> "+consumerRecord.topic()+" : "+msg);
//        ScanMessage scanMessage= JSONObject.parseObject(msg,ScanMessage.class);
////        String projectId=scanMessage.getProjectId();
////        String commitId=scanMessage.getCommitId();
////        initialProject(projectId);
////        scan(projectId,commitId);
    }


    /**
     * 根据扫描的结果更新project的状态
     * @author WZY
     */
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
            projectParam.put("scan_status","Scanned");
        }else{
            if(scanResult.getDescription().equals("Mapping failed")){
                //mapping 失败，删除当前project当前所扫commit得到的rawIssue和location
                restTemplate.delete(rawIssueServicePath+"/"+projectId);
            }
            projectParam.put("scan_status",scanResult.getDescription());
        }
        updateProject(projectParam);
    }
}
