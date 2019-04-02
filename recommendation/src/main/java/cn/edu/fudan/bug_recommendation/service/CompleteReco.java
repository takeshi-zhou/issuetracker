package cn.edu.fudan.bug_recommendation.service;

import cn.edu.fudan.bug_recommendation.domain.Recommendation;

import java.util.List;

public interface CompleteReco {
    Recommendation completeCode(Recommendation info);
}
