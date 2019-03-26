package cn.edu.fudan.recommendation.dao;

import cn.edu.fudan.recommendation.domain.BugRecommendation;
import cn.edu.fudan.recommendation.mapper.RecommendationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RecommendationDao {

    private RecommendationMapper recommendationMapper;

    @Autowired
    public void setRecommendationMapper(RecommendationMapper recommendationMapper){
        this.recommendationMapper = recommendationMapper;
    }

    public void addBugRecommendation(BugRecommendation recommendation){
        recommendationMapper.addBugRecommendation(recommendation);
    }

    public boolean isLocationExist(String location){
        return getRecommendationsByLocation(location).size() !=0;
    }

    public boolean isTypeExist(String type){
        return getRecommendationsByType(type).size()!=0;
    }

//    public Recommendation getRecommendationByType(String type){
//        return recommendationMapper.getRecommendationByType(type);
//    }

    public List<BugRecommendation> getRecommendationsByType(String type) {
        List<BugRecommendation> recommendations = recommendationMapper.getRecommendationsByType(type);
        List<BugRecommendation> list = new ArrayList<>();
        for (BugRecommendation recommendation : recommendations) {
            list.add(recommendation);
        }
        return list;
    }

    public List<BugRecommendation> getRecommendationsByLocation(String location){
        List<BugRecommendation> recommendations = recommendationMapper.getRecommendationByLocation(location);
        List<BugRecommendation> list = new ArrayList<>();
        for (BugRecommendation recommendation : recommendations) {
            list.add(recommendation);
        }
        return list;
    }
}
