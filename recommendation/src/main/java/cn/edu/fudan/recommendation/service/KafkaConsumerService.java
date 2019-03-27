/**
 * @description:
 * @author: fancying
 * @create: 2019-03-26 15:38
 **/
package cn.edu.fudan.recommendation.service;

import cn.edu.fudan.recommendation.dao.RecommendationDao;
import cn.edu.fudan.recommendation.domain.BugRecommendation;
import cn.edu.fudan.recommendation.domain.BugSolvedBasicInfo;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumerService {

    RecommendationDao recommendationDao;

    @Autowired
    public void setRecommendationDao(RecommendationDao recommendationDao) {
        this.recommendationDao = recommendationDao;
    }

    @KafkaListener(id = "bugSolvedInfo", topics = {"solvedBug"}, groupId = "recommendation" )
    public void bugSolvedInfo(ConsumerRecord<String, String> consumerRecord) {
        String msg = consumerRecord.value();
        log.info("receive message from topic -> " + consumerRecord.topic() + " : " + msg);
        BugSolvedBasicInfo basicInfo = JSONObject.parseObject(msg, BugSolvedBasicInfo.class);
        if (isSolved(basicInfo.getPreCode(),basicInfo.getCurCode()))
            recommendationDao.addBugRecommendation(new BugRecommendation());
    }

    private boolean isSolved(String preCode, String curCode) {
        return true;
    }
}