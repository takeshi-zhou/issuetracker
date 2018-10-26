package cn.edu.fudan.eventservice.domain;

public enum EventType {

    NEW_BUG("add a bug"),
    ELIMINATE_BUG("eliminate a bug"),
    MODIFY_BUG("modify a bug"),
    NEW_CLONE_CLASS("add a clone class"),
    NEW_CLONE_INSTANCE("add a clone instance"),
    MODIFY_CLONE_CLASS("modify a clone class"),
    MODIFY_CLONE_INSTANCE("modify a clone instance"),
    REMOVE_CLONE_INSTANCE("remove a clone instance"),
    REMOVE_CLONE_CLASS("remove a clone class");


    private String description;

    EventType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
