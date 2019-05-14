package cn.edu.fudan.bug_recommendation.service;

import cn.edu.fudan.bug_recommendation.domain.Recommendation;

import java.util.List;


public interface RecommendationService {

    void addBugRecommendation(Recommendation recommendation);

    boolean isLocationExist(String location);

    boolean isTypeExist(String type);

    List<Recommendation> getRecommendationsByType(String type);

}
