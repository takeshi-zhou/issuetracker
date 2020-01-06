package cn.edu.fudan.measureservice.mapper;

import cn.edu.fudan.measureservice.domain.CommitBase;
import cn.edu.fudan.measureservice.domain.CommitInfoDeveloper;
import cn.edu.fudan.measureservice.domain.Developer;
import cn.edu.fudan.measureservice.domain.RepoMeasure;
import cn.edu.fudan.measureservice.domain.test.Commit;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RepoMeasureMapper {

    int sameMeasureOfOneCommit(@Param("repo_id")String repo_id,@Param("commit_id")String commit_id);

    RepoMeasure getRepoMeasureByCommit(@Param("repo_id")String repo_id,@Param("commit_id")String commit_id);

    CommitBase getCommitBaseInformation(@Param("repo_id")String repo_id,@Param("commit_id")String commit_id);

    List<CommitInfoDeveloper> getCommitInfoDeveloperListByDuration(@Param("repo_id")String repo_id,@Param("since")String since,@Param("until")String until,@Param("developer_name")String developer_name);

    Integer getAddLinesByDuration(@Param("repo_id")String repo_id,@Param("since")String since,@Param("until")String until);

    Integer getDelLinesByDuration(@Param("repo_id")String repo_id,@Param("since")String since,@Param("until")String until);

    int getCommitCountsByDuration(@Param("repo_id")String repo_id,@Param("since")String since,@Param("until")String until,@Param("developer_name")String developerName);

    String getStartDateOfRepo(@Param("repo_id")String repo_id);

    void insertOneRepoMeasure(RepoMeasure repoMeasure);

    RepoMeasure getLatestMeasureData(@Param("repo_id")String repo_id);

    RepoMeasure getFirstMeasureDataAfterDuration(@Param("repo_id")String repo_id,@Param("time_line") Date time_line);

    List<RepoMeasure> getRepoMeasureBetween(@Param("repo_id")String repo_id,@Param("since")String since,@Param("until")String until);

    void delRepoMeasureByRepoId(@Param("repo_id")String repo_id);

    /**
     * 根据 开发者名字与repo id获取项目度量列表，都可以为null
     *
     * @param repo_id
     * @param developer_name
     * @param counts
     *
     */
    List<RepoMeasure> getRepoMeasureByDeveloperAndRepoId(@Param("repo_id")String repo_id,@Param("developer_name")String developer_name,@Param("counts")int counts,@Param("since")String since,@Param("until")String until);

    List<Commit> getCommits(@Param("repo_id")String repo_id);
}
