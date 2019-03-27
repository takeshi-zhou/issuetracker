package cn.edu.fudan.recommendation.service.impl;

import cn.edu.fudan.recommendation.dao.RecommendationDao;
import cn.edu.fudan.recommendation.domain.BugRecommendation;
import cn.edu.fudan.recommendation.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private RecommendationDao recommendationDao;

    @Autowired
    public void setRecommendationDao(RecommendationDao recommendationDao){
        this.recommendationDao=recommendationDao;
    }

    public boolean isLocationExist(String location){
        return recommendationDao.isLocationExist(location);
    }

    public boolean isTypeExist(String type){
        return recommendationDao.isTypeExist(type);
    }

    public List<BugRecommendation> getRecommendationsByType(String type){
        List<BugRecommendation> recommendationsList = recommendationDao.getRecommendationsByType(type);
        return recommendationsList;
    }


    public void addBugRecommendation(BugRecommendation recommendation){
        if (recommendation.getType()==null ||
        recommendation.getLocation()==null ||
        recommendation.getStart_line()==null || recommendation.getEnd_line()==null || recommendation.getCurr_commitid()==null ||
        recommendation.getNext_commitid()==null)
        {
            throw new RuntimeException("param loss");
        }

        if (isLocationExist(recommendation.getLocation()) && isTypeExist(recommendation.getType()))
            throw new RuntimeException("This error message already exists");
        recommendation.setUuid(UUID.randomUUID().toString());

        recommendationDao.addBugRecommendation(recommendation);

    }


}