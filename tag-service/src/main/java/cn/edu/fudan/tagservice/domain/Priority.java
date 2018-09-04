package cn.edu.fudan.tagservice.domain;

import java.util.HashMap;
import java.util.Map;

public enum Priority{
    Immediate("red"),
    Urgent("2"),
    High("3"),
    Normal("4"),
    Low("5"),
    Ignore("6"),
    Misinformation("7");

    private String color;
    private static Map<String,Priority> valueMap = new HashMap<>();

    static {
        for (Priority priority : Priority.values()){
            valueMap.put(priority.toString(),priority);
        }
    }

    Priority(String color){
        this.color=color;
    }

    public static boolean contains(String name) {
        return valueMap.keySet().contains(name);
    }

    public String getColor() {
        return color;
    }

    public static Priority getByValue(String value){
        Priority result = valueMap.get(value);
        if (result == null){
            throw new IllegalArgumentException("No element matches "+value);
        }
        return result;
    }

}
