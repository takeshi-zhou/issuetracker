package cn.edu.fudan.cloneservice.task;

import cn.edu.fudan.cloneservice.domain.Scan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component("PackageScanTask")
public class PackageScanTask {
    //TODO add Logger
  //  private static final Logger logger= LoggerFactory.getLogger(CloneScanTask.class);

    //#1 先写scan方法
    private void startScan(String repoId, String repoName, String repoPath) {



    }

    //#2 考虑上锁 释放锁

    //#3 获取分析结果 存入数据库




}
