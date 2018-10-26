package cn.edu.fudan.eventservice.service.impl;

import cn.edu.fudan.eventservice.dao.EventDao;
import cn.edu.fudan.eventservice.domain.Event;
import cn.edu.fudan.eventservice.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author WZY
 * @version 1.0
 **/
@Slf4j
@Service
public class EventServiceImpl implements EventService {

    private EventDao eventDao;

    public EventServiceImpl(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    @Override
    public void addEvents(List<Event> events) {
        eventDao.addEvents(events);
    }

    @Override
    public List<Event> getCurrentEvents(List<String> repoIds) {
        if(repoIds==null||repoIds.isEmpty())
            return Collections.emptyList();
        return eventDao.getCurrentEvents(repoIds);
    }
}
