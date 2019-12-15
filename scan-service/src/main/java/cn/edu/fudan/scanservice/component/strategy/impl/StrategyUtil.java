package cn.edu.fudan.scanservice.component.strategy.impl;

import cn.edu.fudan.scanservice.domain.ScanMessageWithTime;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class StrategyUtil {

    public static String getRepoIdByMsg(Map<LocalDate, List<ScanMessageWithTime>> map){
        for(Map.Entry<LocalDate, List<ScanMessageWithTime>> entry : map.entrySet()){
            List<ScanMessageWithTime> scanMessageWithTimes = entry.getValue();
            if(scanMessageWithTimes != null && !scanMessageWithTimes.isEmpty()){
                return scanMessageWithTimes.get(0).getRepoId();
            }
        }
        return null;
    }
}
