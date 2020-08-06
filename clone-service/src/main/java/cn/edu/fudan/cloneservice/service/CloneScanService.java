package cn.edu.fudan.cloneservice.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface CloneScanService {

    /**
     * cloneMessageListener
     *
     * @param consumerRecord get consumer record
     */
    void cloneMessageListener(ConsumerRecord<String, String> consumerRecord);
}
