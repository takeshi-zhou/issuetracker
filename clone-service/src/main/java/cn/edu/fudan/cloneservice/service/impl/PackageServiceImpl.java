package cn.edu.fudan.cloneservice.service.impl;

import cn.edu.fudan.cloneservice.dao.PackageScanStatusDao;
import cn.edu.fudan.cloneservice.domain.CloneScanInfo;
import cn.edu.fudan.cloneservice.domain.PackageScanStatus;
import cn.edu.fudan.cloneservice.domain.ScanInitialInfo;
import cn.edu.fudan.cloneservice.service.PackageService;
import cn.edu.fudan.cloneservice.task.PackageScanTask;
import cn.edu.fudan.cloneservice.task.ScanTask;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PackageServiceImpl implements PackageService {

    private KafkaTemplate kafkaTemplate;
    private PackageScanTask packageScanTask;

    @Autowired
    private ScanTask scanTask;


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
    @KafkaListener(id = "cloneScan", topics = {"CloneZNJ"}, groupId = "clone")
    public void cloneMessageListener(ConsumerRecord<String, String> consumerRecord) {
        String msg = consumerRecord.value();

        CloneScanInfo cloneScanInfo = JSONObject.parseObject(msg, CloneScanInfo.class);

        String repoId = cloneScanInfo.getRepoId();

        List<String> commits = cloneScanInfo.getCommitIds();

        for(String commitId:commits){
            scanTask.runSynchronously(repoId, commitId, "clone");
        }


    }


    @SuppressWarnings("unchecked")
    @Override
    @KafkaListener(id = "rePackageScan", topics = {"CloneZNJReScan"}, groupId = "clone")
    public void ReCloneMessageListener(ConsumerRecord<String, String> consumerRecord) {
//        String msg = consumerRecord.value();
////        System.out.println(msg);
//        ScanInitialInfo scanInitialInfo = JSONObject.parseObject(msg, ScanInitialInfo.class);
//
//        //#0 收到要扫描的项目信息
//        //1 repo id
//        //2 repo 名字
//        //3 repo 路径
//        String repoId=scanInitialInfo.getRepoId();
//        List<String> commitList = scanInitialInfo.getCommitList();//从数据库拉取commit列表
//        //#1 只更新克隆检测信息 包信息已经全了
//
//        packageScanTask.runRe(repoId,commitList);

    }

}