package cn.edu.fudan.measureservice.service;

import cn.edu.fudan.measureservice.domain.ActiveMeasure;
import cn.edu.fudan.measureservice.domain.Duration;
import cn.edu.fudan.measureservice.domain.Granularity;
import cn.edu.fudan.measureservice.domain.RepoMeasure;

import java.util.List;

public interface MeasureService {

    Object getMeasureDataChange(String userToken, Duration duration);

    void saveMeasureData(String repoId,String commitId,String commitTime);

    List<RepoMeasure> getRepoMeasureByRepoId(String repoId,Duration duration);

    List<String> getModulesOfRepo(String repoId);

    List<ActiveMeasure> getActiveMeasure(String repoId, String since, String until, Granularity granularity);
}
