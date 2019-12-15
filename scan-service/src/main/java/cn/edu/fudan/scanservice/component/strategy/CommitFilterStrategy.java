package cn.edu.fudan.scanservice.component.strategy;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CommitFilterStrategy <T> {

    /**
     * filter
     *
     * @param map ?
     * @param dates get local date
     * @return List<T>
     */
    List<T> filter(Map<LocalDate, List<T>> map, List<LocalDate> dates);
}
