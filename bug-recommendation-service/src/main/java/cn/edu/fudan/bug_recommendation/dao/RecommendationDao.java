package cn.edu.fudan.bug_recommendation.dao;

import cn.edu.fudan.bug_recommendation.domain.Recommendation;
import cn.edu.fudan.bug_recommendation.mapper.RecommendationMapper;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        return getRecommendationsByLocation(location).size() != 0;
    }

    public boolean isTypeExist(Map<String, Object> map){
        return getRecommendationsByType(map).size()!=0;
    }

    public boolean isBuglinesExist(String bug_lines){
        return getRecommendationsByBuglines(bug_lines).size()!=0;
    }

    public boolean isStart_lineExist(Integer start_line){
        return getRecommendationsStart_line(start_line).size()!=0;
    }

    public boolean isEnd_lineExist(Integer end_line){
        return getRecommendationsEnd_line(end_line).size()!=0;
    }

    public String getRepoNameByRepoId(String repoId){
        String reponame = recommendationMapper.getRepoNameByRepoId(repoId);
        return reponame;
    }

//    public Recommendation getRecommendationByType(String type){
//        return recommendationMapper.getRecommendationByType(type);
//    }
    public List<Recommendation> getRecommendationsEnd_line(Integer end_line){
        List<Recommendation> recommendations = recommendationMapper.getRecommendationsByEnd_line(end_line);
        List<Recommendation> list = new ArrayList<>();
        for (Recommendation recommendation : recommendations) {
            list.add(recommendation);
        }
        return list;
    }

    public List<Recommendation> getRecommendationsStart_line(Integer start_line){
        List<Recommendation> recommendations = recommendationMapper.getRecommendationsByStart_line(start_line);
        List<Recommendation> list = new ArrayList<>();
        for (Recommendation recommendation : recommendations) {
            list.add(recommendation);
        }
        return list;
    }


    public List<Recommendation> getRecommendationsByType(Map<String, Object> map) {
        List<Recommendation> recommendations = recommendationMapper.getRecommendationsByType(map);
        List<Recommendation> list = new ArrayList<>();
        for (Recommendation recommendation : recommendations) {
            if (recommendation.getModification_method().equals("modify")) {
                list.add(recommendation);
            }
        }
        return list;
    }

    public List<Recommendation> getRecommendationsByBuglines(String bug_lines) {
        List<Recommendation> recommendations = recommendationMapper.getRecommendationsByBug_lines(bug_lines);
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
    public List<Recommendation> getRecommendationsSameTypeFile(String type,String filename){
        List<Recommendation> recommendations = recommendationMapper.getRecommendationsSameTypeFile(type,filename);
        List<Recommendation> list = new ArrayList<>();
        for (Recommendation recommendation : recommendations) {
            list.add(recommendation);
        }
        return list;
    }
    public Integer getRecommendationsByTypeCount(String type){
        return recommendationMapper.getRecommendationsByTypeCount(type);
    }
    public void updateRecommendationsAppearNum(Integer appear_num,String uuid){
        recommendationMapper.updateRecommendationsAppearNum(appear_num,uuid);
    }
}
