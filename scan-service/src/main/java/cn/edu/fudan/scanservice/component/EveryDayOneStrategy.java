package cn.edu.fudan.scanservice.component;

import cn.edu.fudan.scanservice.domain.ScanMessageWithTime;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component("EVERYDAY")
public class EveryDayOneStrategy implements  CommitFilterStrategy<ScanMessageWithTime>{


    @Override
    public List<ScanMessageWithTime>  filter(Map<LocalDate, List<ScanMessageWithTime>> map, List<LocalDate> dates) {
        int sourceSize=dates.size();
        LinkedList<ScanMessageWithTime> result=new LinkedList<>();
        int i=0;
        while(i<sourceSize){
            LocalDate date=dates.get(sourceSize-1-i);
            List<ScanMessageWithTime> list=map.get(date);
            result.addFirst(list.get(list.size()-1));
            i++;
        }
        return result;
    }
}
