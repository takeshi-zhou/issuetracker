package cn.edu.fudan.bug_recommendation.service.impl;

import cn.edu.fudan.bug_recommendation.domain.Recommendation;
import cn.edu.fudan.bug_recommendation.service.CompleteReco;
import cn.edu.fudan.bug_recommendation.service.GetCode;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CompleteRecoImpl implements CompleteReco {

    private GetCode getCode;

    @Autowired
    public void setGetCode(GetCode getCode){
        this.getCode = getCode;
    }

    public Recommendation completeCode(Recommendation info){
        //setPrevCode
        //查一下file_path指的是什么
        String prevCode = getCode.getCode(info.getRepoid(),info.getCurr_commitid(),info.getLocation());
        //String prevCode = getCode.getFileContent("/home/fdse/user/issueTracker/ScanMessageWithTime.java");
        info.setPrev_code(prevCode);
        //setCurrCode
        String currCode = getCode.getCode(info.getRepoid(),info.getNext_commitid(),info.getLocation());
        //String currCode = getCode.getFileContent("/home/fdse/user/issueTracker/ScanMessageWithTime.java");
        info.setCurr_code(currCode);
        System.out.println("prevcode: "+prevCode.length());
        System.out.println("currCode: "+currCode.length());
        String modification_method = getCode.getModification_method();
        System.out.println(modification_method);
        info.setModification_method(modification_method);

        return info;
    }
//    public List<Recommendation> getAllReco(Object param){
//        JSONObject allinfo = JSON.parseObject(JSON.toJSONString(param));
//        JSONArray buginfo = (allinfo.getJSONArray("bugInfo"));
//        ArrayList<Recommendation> recoList = new ArrayList<>();
//        for (int i = 0 ;i<buginfo.size();i++) {
//            Recommendation recommendation = new Recommendation();
//            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(buginfo.get(i)));
//            Object location = jsonObject.getJSONArray("locations").get(0);
//            JSONObject l = JSON.parseObject(JSON.toJSONString(location));
//            recommendation.setBug_lines(l.getString("bug_lines"));
//            //System.out.println("code: "+l.getString("code"));
//            recommendation.setEnd_line(l.getInteger("end_line"));
//            recommendation.setStart_line(l.getInteger("start_line"));
//            recommendation.setLocation(l.getString("file_path"));
//            Object issue = jsonObject.getJSONObject("issue");
//            JSONObject issuejson = JSON.parseObject(JSON.toJSONString(issue));
//            recommendation.setType(issuejson.getString("type"));
//            recommendation.setCurr_commitid(allinfo.getString("commitId"));
//            recommendation.setNext_commitid(allinfo.getString("nextCommitId"));
//            recoList.add(recommendation);
//        }
//        return recoList;
//    }
}
