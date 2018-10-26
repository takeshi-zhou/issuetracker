package cn.edu.fudan.eventservice.controller;

import cn.edu.fudan.eventservice.domain.Event;
import cn.edu.fudan.eventservice.domain.ResponseBean;
import cn.edu.fudan.eventservice.service.EventService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author WZY
 * @version 1.0
 **/
@RestController
public class EventController {

    private EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/event")
    public Object addEvents(@RequestBody List<Event> events){
        try {
            eventService.addEvents(events);
            return new ResponseBean(200, "event add success!", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean(401, "event add failed!", null);
        }
    }

    @PostMapping("/event/current-events")
    public Object getCurrentEvents(@RequestBody List<String> repoIds){
        return eventService.getCurrentEvents(repoIds);
    }
}
