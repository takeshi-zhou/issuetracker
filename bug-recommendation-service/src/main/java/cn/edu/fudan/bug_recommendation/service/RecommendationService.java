package cn.edu.fudan.bug_recommendation.service;

import cn.edu.fudan.bug_recommendation.domain.Recommendation;
import org.apache.kafka.common.protocol.types.Field;

import java.util.List;
import java.util.Map;


public interface RecommendationService {

    void addBugRecommendation(Recommendation recommendation) throws Exception;

    boolean isLocationExist(String location);

    boolean isTypeExist(Map<String, Object> map);

//    List<Recommendation> getRecommendationsByType(String type);
    Object getRecommendationsByType(String type,Integer page,Integer size);
    boolean isBugRecommendationExist(String code1,String code2,String filename,String type);


}
