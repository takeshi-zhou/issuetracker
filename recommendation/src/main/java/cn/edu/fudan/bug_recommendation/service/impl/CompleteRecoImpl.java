package cn.edu.fudan.bug_recommendation.service.impl;

import cn.edu.fudan.bug_recommendation.domain.Recommendation;
import cn.edu.fudan.bug_recommendation.service.CompleteReco;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CompleteRecoImpl implements CompleteReco {
    public List<Recommendation> getAllReco(Object param){
        JSONObject allinfo = JSON.parseObject(JSON.toJSONString(param));
        JSONArray buginfo = (allinfo.getJSONArray("bugInfo"));
        ArrayList<Recommendation> recoList = new ArrayList<>();
        for (int i = 0 ;i<buginfo.size();i++) {
            Recommendation recommendation = new Recommendation();
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(buginfo.get(i)));
            Object location = jsonObject.getJSONArray("locations").get(0);
            JSONObject l = JSON.parseObject(JSON.toJSONString(location));
            recommendation.setBug_lines(l.getString("bug_lines"));
            //System.out.println("code: "+l.getString("code"));
            recommendation.setEnd_line(l.getInteger("end_line"));
            recommendation.setStart_line(l.getInteger("start_line"));
            recommendation.setLocation(l.getString("file_path"));
            recommendation.setCode(l.getString("code"));
            Object issue = jsonObject.getJSONObject("issue");
            JSONObject issuejson = JSON.parseObject(JSON.toJSONString(issue));
            recommendation.setType(issuejson.getString("type"));
            recommendation.setCurr_commitid(allinfo.getString("commitId"));
            recommendation.setNext_commitid(allinfo.getString("nextCommitId"));
            recoList.add(recommendation);
        }
        return recoList;
    }
}
