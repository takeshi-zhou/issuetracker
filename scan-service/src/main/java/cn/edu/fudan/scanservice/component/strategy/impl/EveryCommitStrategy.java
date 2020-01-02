package cn.edu.fudan.scanservice.component.strategy.impl;

import cn.edu.fudan.scanservice.component.strategy.CommitFilterStrategy;
import cn.edu.fudan.scanservice.domain.ScanMessageWithTime;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zyh
 * @date 2019/12/24
 */
@Component("EveryCommit")
public class EveryCommitStrategy implements CommitFilterStrategy<ScanMessageWithTime> {

    @Override
    public List<ScanMessageWithTime>  filter(Map<LocalDate, List<ScanMessageWithTime>> map, List<LocalDate> dates) {
        int sourceSize=dates.size();
        LinkedList<ScanMessageWithTime> result=new LinkedList<>();
        int i=0;
        while(i<sourceSize){
            LocalDate date=dates.get(sourceSize-1-i);
            List<ScanMessageWithTime> list=map.get(date);
            List<ScanMessageWithTime> sortedList = list.stream().sorted(Comparator.comparing(ScanMessageWithTime::getCommitTime).reversed()).collect(Collectors.toList());
            for(ScanMessageWithTime scanMessageWithTime : sortedList){
                result.addFirst(scanMessageWithTime);
            }
            i++;
        }
        return result;
    }
}
