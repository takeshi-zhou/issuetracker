package cn.edu.fudan.recommendation.controller;

import cn.edu.fudan.recommendation.domain.BugRecommendation;
import cn.edu.fudan.recommendation.domain.ResponseBean;
import cn.edu.fudan.recommendation.service.AnalyzeDiffFile;
import cn.edu.fudan.recommendation.service.CompleteReco;
import cn.edu.fudan.recommendation.service.GetCode;
import cn.edu.fudan.recommendation.service.RecommendationService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Slf4j
public class RecommendationController {

    private RecommendationService recommendationService;
    private CompleteReco completeReco;
    private AnalyzeDiffFile analyzeDiffFile;
    private GetCode getCode;

    @Autowired
    public void setRecommendationService(RecommendationService recommendationService){
        this.recommendationService = recommendationService;
    }

    @Autowired
    public void setCompleteReco(CompleteReco completeReco){
        this.completeReco = completeReco;
    }

    @Autowired
    public void setAnalyzeDiffFile(AnalyzeDiffFile analyzeDiffFile){
        this.analyzeDiffFile = analyzeDiffFile;
    }

    @Autowired
    public void setGetCode(GetCode getCode){
        this.getCode = getCode;
    }


    /**
     *  inner service
     * */


    /**
     *  diffPostInfo : bugInfo  nextCommitId  curCommitId
     *  bugInfo : bugLines,type,startLine,endLine,location,code
     * */
    @PostMapping("/bugRecommendation")
    @CrossOrigin
    public Object addBugRecommendation(@RequestBody List<BugRecommendation> list){
        //log.info("diffPostInfo: " + diffPostInfo);
        try {
            for (BugRecommendation info1 : list) {
                recommendationService.addBugRecommendation(info1);
            }
            return new ResponseBean(200, "Congratulations！successful add.", null);
        } catch (Exception e) {
            return new ResponseBean(401, "add failed! " + e.getMessage(), null);
        }
    }
    @GetMapping("/bugRecommendation")
    @CrossOrigin
    public Object getBugRecommendation(@RequestParam("type") String type,
                                       @RequestParam(value = "code", required = false, defaultValue = "") String code){
        return new ResponseBean(200,"success",recommendationService.getRecommendationsByType(type));
    }
}
