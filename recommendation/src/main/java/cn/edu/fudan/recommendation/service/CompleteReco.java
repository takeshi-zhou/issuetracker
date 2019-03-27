package cn.edu.fudan.recommendation.service;

import cn.edu.fudan.recommendation.domain.BugRecommendation;

import java.util.List;

public interface CompleteReco {
    List<BugRecommendation> getAllReco(Object buginfo);
}
