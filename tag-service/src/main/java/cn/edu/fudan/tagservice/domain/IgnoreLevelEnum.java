package cn.edu.fudan.tagservice.domain;

public enum IgnoreLevelEnum {
    USER(1, "user"),
    REPOTORY(2, "repository"),
    PROJECT(3, "project");

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
