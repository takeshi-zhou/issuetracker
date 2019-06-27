package cn.edu.fudan.bug_recommendation.service.impl;

import cn.edu.fudan.bug_recommendation.domain.Recommendation;
import cn.edu.fudan.bug_recommendation.service.CompleteReco;
import cn.edu.fudan.bug_recommendation.service.GetCode;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CompleteRecoImpl implements CompleteReco {
//这里没有注释
    private GetCode getCode;

    @Autowired
    public void setGetCode(GetCode getCode){
        this.getCode = getCode;
    }


    public Recommendation completeCode(Recommendation info){
        //setPrevCode
        //查一下file_path指的是什么
        String prevCode = getCode.getCode(info.getRepoid(),info.getCurr_commitid(),info.getLocation());
        //String prevCode = getCode.getFileContent("C:/Users/LiuShuang/Desktop/a.java");
        info.setPrev_code(prevCode);
        //setCurrCode
        String currCode = getCode.getCode(info.getRepoid(),info.getNext_commitid(),info.getLocation());
        //String currCode = getCode.getFileContent("C:/Users/LiuShuang/Desktop/b.java");
        info.setCurr_code(currCode);
        if(prevCode!=null){
            System.out.println("prevcode: "+prevCode.length());
        }else {
            System.out.println("prevcode: is null");
        }
        if(currCode!=null){
            System.out.println("currCode: "+currCode.length());
        }else {
            System.out.println("currCode: is null");
        }
        String modification_method = getCode.getModification_method();
        //String modification_method = "test";
        System.out.println(modification_method);
        info.setModification_method(modification_method);
        //对buglines进行排序
        String buglines = info.getBug_lines();
        String[] buglinesingle = buglines.split(",");
        int[] buglinesint = new int[buglinesingle.length];
        for(int i=0;i<buglinesingle.length;i++){
            buglinesint[i] = Integer.parseInt(buglinesingle[i]);
        }
        Arrays.sort(buglinesint);
        String result = String.valueOf(buglinesint[0]);
        for(int i = 1; i < buglinesint.length; i++) {
            result = result + "," + String.valueOf(buglinesint[i]);
        }
        info.setBug_lines(result);
        return info;
    }
}
