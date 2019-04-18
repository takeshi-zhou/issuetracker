package cn.edu.fudan.bug_recommendation.service.impl;

import cn.edu.fudan.bug_recommendation.dao.RecommendationDao;
import cn.edu.fudan.bug_recommendation.domain.Recommendation;
import cn.edu.fudan.bug_recommendation.service.RecommendationService;
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

    public List<Recommendation> getRecommendationsByType(String type){
        List<Recommendation> recommendationsList = recommendationDao.getRecommendationsByType(type);
        return recommendationsList;
    }


    public void addBugRecommendation(Recommendation recommendation){
        System.out.println("into addreco");
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