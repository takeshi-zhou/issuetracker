package cn.edu.fudan.cloneservice.service.impl;

import cn.edu.fudan.cloneservice.dao.PackageScanStatusDao;
import cn.edu.fudan.cloneservice.domain.PackageScanStatus;
import cn.edu.fudan.cloneservice.domain.ScanInitialInfo;
import cn.edu.fudan.cloneservice.service.PackageService;
import cn.edu.fudan.cloneservice.task.PackageScanTask;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PackageServiceImpl implements PackageService {
    private KafkaTemplate kafkaTemplate;
    private PackageScanTask packageScanTask;


    @Autowired
    public void setPackageScanTask(PackageScanTask packageScanTask) {
        this.packageScanTask = packageScanTask;
    }

    @Autowired
    public void setKafkaTemplate(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }



    @SuppressWarnings("unchecked")
    @Override
    @KafkaListener(id = "packageScan", topics = {"CloneZNJ"}, groupId = "clone")
    public void cloneMessageListener(ConsumerRecord<String, String> consumerRecord) {
        String msg = consumerRecord.value();
//        System.out.println(msg);
//        logger.info("received message from topic -> " + consumerRecord.topic() + " : " + msg);
        ScanInitialInfo scanInitialInfo = JSONObject.parseObject(msg, ScanInitialInfo.class);

        //#0 收到要扫描的项目信息
        //1 repo id
        //2 repo 名字
        //3 repo 路径
        String repoId=scanInitialInfo.getRepoId();
        List<String> commitList = scanInitialInfo.getCommitList();
        //#1 根据给的信息，启动包的扫描服务
        //把这个对象信息填充好 写一个task并run

        packageScanTask.run(repoId,commitList);

    }


}