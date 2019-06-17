package cn.edu.fudan.bug_recommendation.mapper;

import cn.edu.fudan.bug_recommendation.domain.Recommendation;
import org.apache.ibatis.annotations.Param;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RecommendationMapper {
    void addBugRecommendation(Recommendation recommendation);

    List<Recommendation> getRecommendationsByLocation(@Param("location") String location);

//    Recommendation getRecommendationByType(@Param("type") String type);
//    List<Recommendation> getRecommendationsByType(@Param("type") String type);
    List<Recommendation> getRecommendationsByType(Map<String,Object> map);

    List<Recommendation> getRecommendationsByStart_line(@Param("start_line") Integer start_line);

    List<Recommendation> getRecommendationsByEnd_line(@Param("end_line") Integer end_line);

    List<Recommendation> getRecommendationsByBug_lines(@Param("bug_lines") String bug_lines);

    String getRepoNameByRepoId(@Param("repoid") String repoid);

    Integer getRecommendationsByTypeCount(@Param("type") String type);

    List<Recommendation> getRecommendationsSameTypeFile(@Param("type")String type,@Param("filename")String filename);

    List<String> getAllBugRecommendationId();

    void updateRecommendationsAppearNum(@Param("appear_num")Integer appear_num,@Param("uuid")String uuid);

}
