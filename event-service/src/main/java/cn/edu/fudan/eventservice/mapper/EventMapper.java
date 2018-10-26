package cn.edu.fudan.eventservice.mapper;

import cn.edu.fudan.eventservice.domain.Event;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventMapper {

    int insertEvents(List<Event> list);

    List<Event> getRecentEvents(List<String> list);
}
