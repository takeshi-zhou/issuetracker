package cn.edu.fudan.scanservice.component;

import cn.edu.fudan.scanservice.domain.ScanMessageWithTime;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component("RASWS")
public class RecentAllThenSkipWeekStrategy implements CommitFilterStrategy<ScanMessageWithTime> {




    @Override
    public List<ScanMessageWithTime> filter(Map<LocalDate, List<ScanMessageWithTime>> map, List<LocalDate> dates) {
        int sourceSize=dates.size();
        LocalDate nextTimeLimit=dates.get(sourceSize-1).minusMonths(ScanIntervalForStrategy.getInterval());
        LinkedList<ScanMessageWithTime> result=new LinkedList<>();
        int i=0;
        boolean isRecent=true;
        while(i<sourceSize){
            LocalDate date=dates.get(sourceSize-1-i);
            if(isRecent&&date.isAfter(nextTimeLimit)){
                List<ScanMessageWithTime> list=map.get(date);
//                result.addFirst(list.get(list.size()-1));
                  result = addAllListIntoFirst(result,list);
            }else{
                isRecent=false;
            }
            if(!isRecent){
                if(date.isBefore(nextTimeLimit)||i==sourceSize-1){
                    List<ScanMessageWithTime> list=map.get(date);
                    result.addFirst(list.get(list.size()-1));
                    nextTimeLimit=nextTimeLimit.minusWeeks(1);
                }
            }
            i++;
        }
        return result;
    }

    private LinkedList<ScanMessageWithTime> addAllListIntoFirst(LinkedList<ScanMessageWithTime> result,List<ScanMessageWithTime> scanMessageWithTimes){
        for(ScanMessageWithTime scanMessageWithTime : scanMessageWithTimes){
            result.addFirst(scanMessageWithTime);
        }
        return result;
    }
}
