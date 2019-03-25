package cn.edu.fudan.bug_recommendation.mapper;

import cn.edu.fudan.bug_recommendation.domain.Recommendation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationMapper {
    void addBugRecommendation(Recommendation recommendation);

    Recommendation getRecommendationByLocation(@Param("location") String location);

//    Recommendation getRecommendationByType(@Param("type") String type);
    List<Recommendation> getRecommendationsByType(@Param("type") String type);

    List<String> getAllBugRecommendationId();

}
