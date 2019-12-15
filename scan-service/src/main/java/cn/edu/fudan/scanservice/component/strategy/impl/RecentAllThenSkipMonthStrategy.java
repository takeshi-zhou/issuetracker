package cn.edu.fudan.scanservice.component.strategy.impl;

import cn.edu.fudan.scanservice.component.strategy.CommitFilterStrategy;
import cn.edu.fudan.scanservice.domain.ScanMessageWithTime;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component("RASMS")
public class RecentAllThenSkipMonthStrategy implements CommitFilterStrategy<ScanMessageWithTime> {
    @Override
    public List<ScanMessageWithTime> filter(Map<LocalDate, List<ScanMessageWithTime>> map, List<LocalDate> dates) {
        int sourceSize=dates.size();
        LocalDate oneMonthPoint=dates.get(sourceSize-1).minusMonths(1);
        LocalDate sixMonthsPoint=dates.get(sourceSize-1).minusMonths(6);
        LocalDate nextTimeLimit1=oneMonthPoint;
        LocalDate nextTimeLimit2=sixMonthsPoint;
        LinkedList<ScanMessageWithTime> result=new LinkedList<>();
        int i=0;
        while(i<sourceSize){
            LocalDate date=dates.get(sourceSize-1-i);
            if(date.isAfter(oneMonthPoint)){
                //一个月以内
                List<ScanMessageWithTime> list=map.get(date);
                result.addFirst(list.get(list.size()-1));
            }else if(date.isAfter(sixMonthsPoint)){
                //一个月后，6个月之前
                if(date.isBefore(nextTimeLimit1)){
                    List<ScanMessageWithTime> list=map.get(date);
                    result.addFirst(list.get(list.size()-1));
                    nextTimeLimit1=date.minusWeeks(1);
                }
            }else{
                //6个月之后
                if(date.isBefore(nextTimeLimit2)){
                    List<ScanMessageWithTime> list=map.get(date);
                    result.addFirst(list.get(list.size()-1));
                    nextTimeLimit2=date.minusMonths(1);
                }
            }
            i++;
        }
        return result;
    }
}
