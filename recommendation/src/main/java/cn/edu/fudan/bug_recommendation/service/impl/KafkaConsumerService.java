package cn.edu.fudan.bug_recommendation.service.impl;

import cn.edu.fudan.bug_recommendation.dao.RecommendationDao;
import cn.edu.fudan.bug_recommendation.domain.BugSolvedBasicInfo;
import cn.edu.fudan.bug_recommendation.domain.Recommendation;
import cn.edu.fudan.bug_recommendation.service.CompleteReco;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class KafkaConsumerService {
    RecommendationDao recommendationDao;
    private CompleteReco completeReco;

    @Autowired
    public void setRecommendationDao(RecommendationDao recommendationDao) {
        this.recommendationDao = recommendationDao;
    }
    @Autowired
    public void setCompleteReco(CompleteReco completeReco){
        this.completeReco = completeReco;
    }

    @KafkaListener(id = "bugSolvedInfo", topics = {"solvedBug"}, groupId = "recommendation")
    public void bugSolvedInfo(ConsumerRecord<String,String> consumerRecord){
        String msg = consumerRecord.value();
        System.out.println("receive message from topic -> " + consumerRecord.topic() + " : " + msg);
        List<Recommendation> list = JSONArray.parseObject(msg,List.class);
        if (list!=null){
            for (Recommendation info: list){
                Recommendation newInfo = completeReco.completeCode(info);
                recommendationDao.addBugRecommendation(newInfo);
            }
        }

    }
}
