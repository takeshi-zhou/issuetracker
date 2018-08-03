package cn.edu.fudan.scanservice.service;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface KafkaService {

    void sendScanMessage(JSONObject requestParam);

    void updateCommitScanStatus(ConsumerRecord<String, String> consumerRecord);
}
