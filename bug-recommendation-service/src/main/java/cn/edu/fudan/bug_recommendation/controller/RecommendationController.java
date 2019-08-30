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
    public void addBugRecommendation(@RequestBody List<Recommendation> list){
    }
//    @GetMapping("/get-bug-recommendation")
//    @CrossOrigin
//
//    public Object getBugRecommendation(@RequestParam("type") String type,
//                                       @RequestParam("page") Integer page,
//                                       @RequestParam("size") Integer size){
//        return new ResponseBean(200,"success",recommendationService.getRecommendationsByType(type,page,size));
//    }
    @GetMapping("/get-bug-recommendation-OrderBySimilarity")
    @CrossOrigin
    //给我codeUrl
    public Object getBugRecommendationOrderBySimilarity(@RequestParam("type") String type,
                                       @RequestParam("page") Integer page,
                                       @RequestParam("size") Integer size,
                                       @RequestParam("repoId") String repoId,
                                       @RequestParam("commit_id") String commit_id,
                                       @RequestParam("location") String location,
                                       @RequestParam("start_line") Integer start_line,
                                       @RequestParam("end_line") Integer end_line){
        return new ResponseBean(200,"success",recommendationService.getBugRecommendationOrderBySimilarity(type,page,size,repoId,commit_id,location,start_line,end_line));
    }
//    public Object getBugRecommendationOrderBySimilarity(@RequestParam("type") String type,
//                                                        @RequestParam("page") Integer page,
//                                                        @RequestParam("size") Integer size,
//                                                        @RequestParam("codeUrl") String codeUrl,
//                                                        @RequestParam("start_line") Integer start_line,
//                                                        @RequestParam("end_line") Integer end_line){
//        return new ResponseBean(200,"success",recommendationService.getBugRecommendationOrderBySimilarity(type,page,size,codeUrl,start_line,end_line));
//    }
    @PostMapping("/add-useful-count")
    @CrossOrigin
    public Object addUsefulCount(@RequestBody Map<String,String> map) {
        try {
                recommendationService.addUsefulCount(map.get("uuid"),Integer.valueOf(map.get("usefulcount")));
                return new ResponseBean(200, "Congratulations！successful add useful count.", null);
        }catch (Exception e) {
            return new ResponseBean(401, "failed! " + e.getMessage(), null);
            }
        }
    @DeleteMapping(value = {"inner/bugRecommendation/{repoId}"})
    public Object deleteBugRecommendation(@PathVariable("repoId") String repoId){
        try {
            recommendationService.deleteBugRecommendationByRepoId(repoId);
            return new ResponseBean(200,"bugRecommendation delete success!",null);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseBean(401,"bugRecommendation delete failed!",null);
        }
    }


    }
