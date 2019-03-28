package cn.edu.fudan.bug_recommendation.controller;

import cn.edu.fudan.bug_recommendation.domain.Recommendation;
import cn.edu.fudan.bug_recommendation.domain.ResponseBean;
import cn.edu.fudan.bug_recommendation.service.AnalyzeDiffFile;
import cn.edu.fudan.bug_recommendation.service.CompleteReco;
import cn.edu.fudan.bug_recommendation.service.GetCode;
import cn.edu.fudan.bug_recommendation.service.RecommendationService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/bugRecommendation")
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


    @PostMapping("/add-bug-recommendation")
    @CrossOrigin
    public Object addBugRecommendation(@RequestBody List<Recommendation> list){
//        System.out.println("diffPostInfo: "+diffPostInfo);
//        List<Recommendation> list = completeReco.getAllReco(diffPostInfo);
//        System.out.println("list size: "+list.size());
//        for (Recommendation reco:list) {
//            System.out.println("type: "+reco.getType());
//            String repopath = reco.getLocation();
//            String bug_lines = reco.getBug_lines();
//            String nextcommitid = reco.getNext_commitid();
//            String commitid = reco.getCurr_commitid();
//            JSONObject json = analyzeDiffFile.getDiffRange(repopath,nextcommitid,commitid,bug_lines);
//            if(json.getInteger("nextstart_line")!=0){
//                reco.setStart_line(json.getInteger("start_line"));
//                reco.setEnd_line(json.getInteger("end_line"));
//                reco.setNextstart_line(json.getInteger("nextstart_line"));
//                reco.setNextend_line(json.getInteger("nextend_line"));
//                reco.setDescription(json.getString("description"));
//                String prevcode = getCode.getCodePrev(repopath,commitid,nextcommitid,json.getInteger("start_line"),json.getInteger("end_line"));
//                String currcode = getCode.getCodeCurr(repopath,commitid,nextcommitid,json.getInteger("nextstart_line"),json.getInteger("nextend_line"));
//                reco.setPrev_code(prevcode);
//                reco.setCurr_code(currcode);
//                recommendationService.addBugRecommendation(reco);
//            }
//        }

        try {
            for (Recommendation info: list){
                recommendationService.addBugRecommendation(info);
            }
            return new ResponseBean(200, "Congratulations！successful add.", null);
        }catch (Exception e) {
            return new ResponseBean(401, "add failed! " + e.getMessage(), null);
       }

    }
    @GetMapping("/get-bug-recommendation")
    @CrossOrigin
    public Object getBugRecommendation(@RequestParam("type") String type){
        return new ResponseBean(200,"success",recommendationService.getRecommendationsByType(type));
    }
}
