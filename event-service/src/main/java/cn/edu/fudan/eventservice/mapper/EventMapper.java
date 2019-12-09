package cn.edu.fudan.eventservice.mapper;

import cn.edu.fudan.eventservice.domain.Event;
import cn.edu.fudan.eventservice.domain.EventType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventMapper {

    /**
     * insertEvents
     *
     * @param list get event list
     * @return int
     */
    int insertEvents(List<Event> list);

    /**
     * delete event by repo id and category
     *
     * @param category get event category
     * @param repo_id get event repo id
     */
    void deleteEventByRepoIdAndCategory(@Param("category")String category,@Param("repo_id")String repo_id);

    /**
     * get recent events by event type
     *
     * @param list get event list
     * @param eventType get event type
     * @return List<Event>
     */
    List<Event> getRecentEventsByEventType(@Param("list") List<String> list, @Param("event_type")EventType eventType);

    
}
