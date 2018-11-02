package cn.edu.fudan.eventservice.domain;

import java.util.List;

/**
 * @author WZY
 * @version 1.0
 **/
public class EventsDto {

    private boolean hasChanged;
    private List<Event> events;

    public EventsDto(boolean hasChanged, List<Event> events) {
        this.hasChanged = hasChanged;
        this.events = events;
    }

    public boolean isHasChanged() {
        return hasChanged;
    }

    public void setHasChanged(boolean hasChanged) {
        this.hasChanged = hasChanged;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
