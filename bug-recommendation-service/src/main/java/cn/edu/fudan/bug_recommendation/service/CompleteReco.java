package cn.edu.fudan.bug_recommendation.service;

import cn.edu.fudan.bug_recommendation.domain.Recommendation;

import java.util.List;
//得到两个文件
public interface CompleteReco {
    Recommendation completeCode(Recommendation info);
}
