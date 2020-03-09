package cn.edu.fudan.scanservice.service;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface KafkaService {

    void scanByRequest(JSONObject requestParam);

    void scanByMQ(ConsumerRecord<String, String> consumerRecord);

    void firstScanByMQ(ConsumerRecord<String, String> consumerRecord);

    void updateCommitScanStatus(ConsumerRecord<String, String> consumerRecord);

    String startAutoScan(String repoId,String commitId);
}
