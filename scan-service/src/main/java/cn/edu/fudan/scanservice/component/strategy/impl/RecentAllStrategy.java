package cn.edu.fudan.scanservice.component.strategy.impl;

import cn.edu.fudan.scanservice.component.rest.RestInterfaceManager;
import cn.edu.fudan.scanservice.component.strategy.CommitFilterStrategy;
import cn.edu.fudan.scanservice.component.strategy.ScanIntervalForStrategy;
import cn.edu.fudan.scanservice.domain.ScanMessageWithTime;
import cn.edu.fudan.scanservice.util.DateTimeUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("RAS")
public class RecentAllStrategy implements CommitFilterStrategy<ScanMessageWithTime> {
    private Logger logger = LoggerFactory.getLogger(RecentAllThenSkipWeekStrategy.class);

    private RestInterfaceManager restInterfaceManager;

    @Autowired
    public RecentAllStrategy(
                            RestInterfaceManager restInterfaceManager
                            ) {

        this.restInterfaceManager=restInterfaceManager;

    }

    @Override
    public List<ScanMessageWithTime> filter(Map<LocalDate, List<ScanMessageWithTime>> map, List<LocalDate> dates) throws RuntimeException{
        String repoId = StrategyUtil.getRepoIdByMsg(map);
        LinkedList<ScanMessageWithTime> result=new LinkedList<>();
        LocalDate latestCommitTime = null;
        if(repoId != null ){
            JSONObject jsonObject = restInterfaceManager.getCommitsOfRepoByConditions(repoId,1,1,null);
            if(jsonObject == null){
                throw new RuntimeException("request base server failed");
            }
            JSONArray scanMessageWithTimeJsonArray = jsonObject.getJSONArray("data");
            JSONObject latestScanMessageWithTime = scanMessageWithTimeJsonArray.getJSONObject(0);
            String completeCommitTime = latestScanMessageWithTime.getString("commit_time");
            String commitTime = completeCommitTime.split(" ")[0];
            latestCommitTime = LocalDate.parse(commitTime, DateTimeUtil.Y_M_D_formatter);
        }
        if(latestCommitTime == null){
            return result;
        }

        int sourceSize=dates.size();

        ScanIntervalForStrategy.setInterval(3);
        LocalDate nextTimeLimit=latestCommitTime.minusMonths(ScanIntervalForStrategy.getInterval());

        if(dates.get(dates.size()-1).isBefore(nextTimeLimit)){
            return result;
        }

        int i=0;
        boolean isRecent=true;
        while(i<sourceSize){
            LocalDate date=dates.get(sourceSize-1-i);
            if(isRecent && date.isAfter(nextTimeLimit)){
                List<ScanMessageWithTime> list=map.get(date);
                result = addAllListIntoFirst(result,list);
            }else{
                isRecent=false;
            }

            i++;
        }
        return result;
    }

    private LinkedList<ScanMessageWithTime> addAllListIntoFirst(LinkedList<ScanMessageWithTime> result,List<ScanMessageWithTime> scanMessageWithTimes){
        List<ScanMessageWithTime> sortedList = scanMessageWithTimes.stream().sorted(Comparator.comparing(ScanMessageWithTime::getCommitTime).reversed()).collect(Collectors.toList());
        for(ScanMessageWithTime scanMessageWithTime : sortedList){
            result.addFirst(scanMessageWithTime);
        }
        return result;
    }



}
