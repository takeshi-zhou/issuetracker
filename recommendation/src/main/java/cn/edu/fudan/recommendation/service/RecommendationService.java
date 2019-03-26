package cn.edu.fudan.recommendation.service;

import cn.edu.fudan.recommendation.domain.BugRecommendation;

import java.util.List;


public interface RecommendationService {

    void addBugRecommendation(BugRecommendation recommendation);

    boolean isLocationExist(String location);

    boolean isTypeExist(String type);

    List<BugRecommendation> getRecommendationsByType(String type);

}
