package cn.edu.fudan.tagservice.domain;

public enum IgnoreLevelEnum {
    REPOSITORY(1, "repository"),
    PROJECT(2, "project");

    private final int value;
    private final String level;

    private IgnoreLevelEnum(int value, String level){
        this.value = value;
        this.level = level;
    }

    public int value() {
        return this.value;
    }

    public String level() {
        return this.level;
    }



}
