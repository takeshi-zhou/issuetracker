package cn.edu.fudan.scanservice.component;
import	java.util.Comparator;

import cn.edu.fudan.scanservice.domain.ScanMessageWithTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("RASWS")
public class RecentAllThenSkipWeekStrategy implements CommitFilterStrategy<ScanMessageWithTime> {

    private Logger logger = LoggerFactory.getLogger(RecentAllThenSkipWeekStrategy.class);


    @Override
    public List<ScanMessageWithTime> filter(Map<LocalDate, List<ScanMessageWithTime>> map, List<LocalDate> dates) {
        int sourceSize=dates.size();
        ScanIntervalForStrategy.setInterval(11);
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
//                    List<ScanMessageWithTime> list=map.get(date);
//                    result.addFirst(list.get(list.size()-1));
                    nextTimeLimit=nextTimeLimit.plusWeeks(1);
                    logger.info("nextTimeLimit --> {}",nextTimeLimit.getYear()+"-"+nextTimeLimit.getMonthValue()+"-"+nextTimeLimit.getDayOfMonth() );
                }
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
