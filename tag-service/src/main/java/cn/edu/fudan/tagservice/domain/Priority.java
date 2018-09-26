package cn.edu.fudan.tagservice.domain;

import java.util.HashMap;
import java.util.Map;

public enum Priority {
    Immediate("#b52a28"),
    Urgent("#e36e6c"),
    High("#eba47c"),
    Normal("#ebcb8d"),
    Low("#a0b08f"),
    Ignore("#628e8b"),
    Solved("#d3d3d3"),
    Misinformation("#5499bd");

    private String color;
    private static Map<String, Priority> valueMap = new HashMap<>();

    static {
        for (Priority priority : Priority.values()) {
            valueMap.put(priority.toString(), priority);
        }
    }

    Priority(String color) {
        this.color = color;
    }

    public static boolean contains(String name) {
        return valueMap.keySet().contains(name);
    }

    public String getColor() {
        return color;
    }

    public static Priority getByValue(String value) {
        Priority result = valueMap.get(value);
        if (result == null) {
            throw new IllegalArgumentException("No element matches " + value);
        }
        return result;
    }

}
