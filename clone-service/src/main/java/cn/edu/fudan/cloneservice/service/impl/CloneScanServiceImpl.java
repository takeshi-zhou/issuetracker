package cn.edu.fudan.cloneservice.service.impl;


import cn.edu.fudan.cloneservice.domain.*;
import cn.edu.fudan.cloneservice.service.CloneScanService;
import cn.edu.fudan.cloneservice.task.CloneScanTask;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author WZY
 * @version 1.0
 **/
@Service
public class CloneScanServiceImpl implements CloneScanService {

    private static final Logger logger= LoggerFactory.getLogger(CloneScanServiceImpl.class);

    private KafkaTemplate kafkaTemplate;
    private CloneScanTask cloneScanTask;

    @Autowired
    public CloneScanServiceImpl(KafkaTemplate kafkaTemplate, CloneScanTask cloneScanTask) {
        this.kafkaTemplate = kafkaTemplate;
        this.cloneScanTask = cloneScanTask;
    }

    @Override
    @KafkaListener(id = "cloneScan", topics = {"Clone"}, groupId = "clone")
    public void cloneMessageListener(ConsumerRecord<String, String> consumerRecord) {
        String msg = consumerRecord.value();
        logger.info("received message from topic -> " + consumerRecord.topic() + " : " + msg);
        ScanInitialInfo scanInitialInfo = JSONObject.parseObject(msg, ScanInitialInfo.class);
        String repoId=scanInitialInfo.getRepoId();
        String repoName=scanInitialInfo.getRepoName();
        String repoPath=scanInitialInfo.getRepoPath();
        Scan scan=scanInitialInfo.getScan();
        //每当接受一个clone的scan消息，启动一个异步任务取执行相关操作
        Future<?> future=cloneScanTask.run(repoId,repoName,repoPath,scan);
        new Thread(() -> {
            try {
                future.get(15, TimeUnit.SECONDS);//设置15s的超时时间
            } catch (TimeoutException e) {
                //因scan超时而抛出异常
                logger.error("超时了");
                future.cancel(false);
                send(repoId,scan.getCommit_id(),"failed","Time Out");
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @SuppressWarnings("unchecked")
    private void send(String repoId, String commitId ,String status, String description) {
        ScanResult scanResult = new ScanResult(repoId, commitId,"clone" ,status, description);
        kafkaTemplate.send("ScanResult", JSONObject.toJSONString(scanResult));
    }
}
