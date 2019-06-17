package cn.edu.fudan.bug_recommendation.service.impl;

import cn.edu.fudan.bug_recommendation.dao.RecommendationDao;
import cn.edu.fudan.bug_recommendation.domain.BugSolvedBasicInfo;
import cn.edu.fudan.bug_recommendation.domain.Recommendation;
import cn.edu.fudan.bug_recommendation.service.AnalyzeDiffFile;
import cn.edu.fudan.bug_recommendation.service.CompleteReco;
import cn.edu.fudan.bug_recommendation.service.GetCode;
import cn.edu.fudan.bug_recommendation.service.RecommendationService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class KafkaConsumerService {
    RecommendationDao recommendationDao;
    private RecommendationService recommendationService;
    private CompleteReco completeReco;
    private AnalyzeDiffFile analyzeDiffFile;
    private GetCode getCode;
    int cnt = 0;

    @Autowired
    public void setRecommendationService(RecommendationService recommendationService){
        this.recommendationService = recommendationService;
    }
    @Autowired
    public void setRecommendationDao(RecommendationDao recommendationDao) {
        this.recommendationDao = recommendationDao;
    }
    @Autowired
    public void setGetCode(GetCode getCode){this.getCode=getCode;}

    @Autowired
    public void setCompleteReco(CompleteReco completeReco){
        this.completeReco = completeReco;
    }

    @Autowired
    public void setAnalyzeDiffFile(AnalyzeDiffFile analyzeDiffFile){
        this.analyzeDiffFile = analyzeDiffFile;
    }

    @KafkaListener(topics = {"solvedBug"})
    public void bugSolvedInfo(ConsumerRecord<String,String> consumerRecord){
            String msg = consumerRecord.value();
            System.out.println("receive message from topic -> " + consumerRecord.topic() + " : " + msg);
            /*
            * solvedBug : [{"repoid"，"next_commitid","curr_commitid"，"bug_lines","start_line","location","type","end_line"}{}...]
            * 通过repoid在project表中查到reponame
            * 通过location切割出filename
            * 通过codeservice拿到code
            * 通过location表拿到method_name
            */
            List<Recommendation> list = JSONObject.parseArray(msg, Recommendation.class);
            if (list != null) {
                for (Recommendation info : list) {
                    info.setAppear_num(1);
                    completeReco.completeCode(info);//补全prev_code,curr_code
                    String repoName = getCode.getRepoName(info.getRepoid());//补全reponame
                    String fileName = getCode.getFileName(info.getLocation());//补全filename
                    info.setReponame(repoName);
                    info.setFilename(fileName);

                    //System.out.println("repoName: " + repoName);
//                JSONObject json = analyzeDiffFile.getDiffRange(newInfo.getLocation(),newInfo.getNext_commitid(),newInfo.getCurr_commitid(),newInfo.getBug_lines());
//                if(json.getInteger("nextstart_line")!=0){
//                    newInfo.setStart_line(json.getInteger("start_line"));
//                    newInfo.setEnd_line(json.getInteger("end_line"));
//                    newInfo.setNextstart_line(json.getInteger("nextstart_line"));
//                    newInfo.setNextend_line(json.getInteger("nextend_line"));
//                    newInfo.setDescription(json.getString("description"));
//                }
                    if (repoName != null) {//reponame非空，则添加到数据库
                        try{
                            recommendationService.addBugRecommendation(info);
                            System.out.println("add reco success!!!");
                        }catch (Exception e){
                            System.out.println("add reco fail!!!");
                            e.printStackTrace();
                        }

                    }else{
                        System.out.println(info.getFilename()+" repoName is null!!!!!!!!");
                    }

                }
            }
    }
}
