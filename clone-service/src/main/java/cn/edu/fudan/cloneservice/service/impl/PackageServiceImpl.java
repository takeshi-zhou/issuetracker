package cn.edu.fudan.cloneservice.service.impl;

import cn.edu.fudan.cloneservice.dao.PackageScanStatusDao;
import cn.edu.fudan.cloneservice.domain.PackageScanStatus;
import cn.edu.fudan.cloneservice.domain.ScanInitialInfo;
import cn.edu.fudan.cloneservice.service.PackageService;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PackageServiceImpl implements PackageService {
    PackageScanStatusDao packageScanStatusDao;
    private KafkaTemplate kafkaTemplate;

    @Autowired
    public void setKafkaTemplate(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public PackageServiceImpl(PackageScanStatusDao packageScanStatusDao){
        this.packageScanStatusDao = packageScanStatusDao;
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
        String repoName=scanInitialInfo.getRepoName();
        String repoPath=scanInitialInfo.getRepoPath();
        List<String> commitList = scanInitialInfo.getCommitList();
        //#1 根据给的信息，启动包的状态查询
        //把这个对象信息填充好 写一个task并run
        try{
            for(String commit_id:commitList)
            {
                String test  = packageScanStatusDao.selectPackageScanStatusByRepoIdAndCommitId(repoId, commit_id);
                System.out.println(test);
            }
        }catch (Exception e){
            e.printStackTrace();
        }



        //#2 如果scan状态未完成。需要一个scan列表，根据这个列表进行scan


//        Scan scan=scanInitialInfo.getScan();

        //#2 如果scan全部完成，则OK！
        //每当接受一个clone的scan消息，启动一个异步任务取执行相关操作

    }
}
