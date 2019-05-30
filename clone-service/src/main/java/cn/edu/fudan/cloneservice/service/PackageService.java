package cn.edu.fudan.cloneservice.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface PackageService {

    void cloneMessageListener(ConsumerRecord<String, String> consumerRecord);
    void ReCloneMessageListener(ConsumerRecord<String, String> consumerRecord);
}
