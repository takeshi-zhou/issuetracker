package cn.edu.fudan.eventservice.service;

import cn.edu.fudan.eventservice.domain.Event;

import java.util.List;

/**
 * @author WZY
 * @version 1.0
 **/
public interface EventService {

    void addEvents(List<Event> events);

    List<Event> getCurrentEvents(List<String> repoIds);
}
