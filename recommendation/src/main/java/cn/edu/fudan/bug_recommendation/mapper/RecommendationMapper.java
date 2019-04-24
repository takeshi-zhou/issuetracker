package cn.edu.fudan.bug_recommendation.mapper;

import cn.edu.fudan.bug_recommendation.domain.Recommendation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationMapper {
    void addBugRecommendation(Recommendation recommendation);

    List<Recommendation> getRecommendationsByLocation(@Param("location") String location);

//    Recommendation getRecommendationByType(@Param("type") String type);
    List<Recommendation> getRecommendationsByType(@Param("type") String type);

    List<Recommendation> getRecommendationsByStart_line(@Param("start_line") Integer start_line);

    List<Recommendation> getRecommendationsByEnd_line(@Param("end_line") Integer end_line);

    List<String> getAllBugRecommendationId();

}
