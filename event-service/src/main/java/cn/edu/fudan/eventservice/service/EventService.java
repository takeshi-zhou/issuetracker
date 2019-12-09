package cn.edu.fudan.eventservice.service;

import cn.edu.fudan.eventservice.domain.Event;

import java.util.List;

/**
 * @author WZY
 * @version 1.0
 **/
public interface EventService {

    /**
     * add events
     *
     * @param events get events list
     */
    void addEvents(List<Event> events);

    /**
     * delete events
     *
     * @param repo_id get event repo id
     * @param category get event category
     */
    void deleteEvents(String repo_id,String category);

    /**
     * get current events
     *
     * @param userToken get event user token
     * @param category get event category
     * @return Object
     */
    Object getCurrentEvents(String userToken,String category);

    /**
     * has new events
     *
     * @param userToken get event user token
     * @param category get event category
     * @return Object
     */
    Object hasNewEvents(String userToken,String category);
}
