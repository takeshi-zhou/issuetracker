package cn.edu.fudan.scanservice.component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CommitFilterStrategy <T> {

    List<T> filter(Map<LocalDate, List<T>> map, List<LocalDate> dates);
}
