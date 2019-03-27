package cn.edu.fudan.recommendation.mapper;

import cn.edu.fudan.recommendation.domain.BugRecommendation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationMapper {
    void addBugRecommendation(BugRecommendation recommendation);

    List<BugRecommendation> getRecommendationByLocation(@Param("location") String location);

//    BugRecommendation getRecommendationByType(@Param("type") String type);
    List<BugRecommendation> getRecommendationsByType(@Param("type") String type);

    List<String> getAllBugRecommendationId();

}
