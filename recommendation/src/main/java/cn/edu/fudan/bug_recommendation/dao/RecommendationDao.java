package cn.edu.fudan.bug_recommendation.dao;

import cn.edu.fudan.bug_recommendation.domain.Recommendation;
import cn.edu.fudan.bug_recommendation.mapper.RecommendationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RecommendationDao {

    private RecommendationMapper recommendationMapper;

    @Autowired
    public void setRecommendationMapper(RecommendationMapper recommendationMapper){
        this.recommendationMapper=recommendationMapper;
    }

    public void addBugRecommendation(Recommendation recommendation){
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

    public List<Recommendation> getRecommendationsByType(String type) {
        List<Recommendation> recommendations = recommendationMapper.getRecommendationsByType(type);
        List<Recommendation> list = new ArrayList<>();
        for (Recommendation recommendation : recommendations) {
            list.add(recommendation);
        }
        return list;
    }

    public List<Recommendation> getRecommendationsByLocation(String location){
        List<Recommendation> recommendations = recommendationMapper.getRecommendationsByLocation(location);
        List<Recommendation> list = new ArrayList<>();
        for (Recommendation recommendation : recommendations) {
            list.add(recommendation);
        }
        return list;
    }
}
