package cn.edu.fudan.tagservice.domain;

import java.util.HashMap;
import java.util.Map;

//枚举类名带上Enum后缀，枚举成员名称需要全大写，单词间用下划线隔开
public enum PriorityEnum {
    IMMEDIATE("#b52a28",0),
    URGENT("#e36e6c",1),
    HIGH("#eba47c",2),
    NORMAL("#ebcb8d",3),
    LOW("#a0b08f",4),
    IGNORE("#628e8b",5),
    SOLVED("#d3d3d3",6),
    MISINFORMATION("#5499bd",7);

    private String color;
    private int level;
    private static Map<String, PriorityEnum> valueMap = new HashMap<>();

    static {
        for (PriorityEnum priority : PriorityEnum.values()) {
            valueMap.put(priority.toString(), priority);
        }
    }

    PriorityEnum(String color, int level) {
        this.color = color;
        this.level=level;
    }

    public static boolean contains(String name) {
        return valueMap.keySet().contains(name);
    }

    public String getColor() {
        return color;
    }

    public int getLevel() {
        return level;
    }

    public static PriorityEnum getByValue(String value) {
        PriorityEnum result = valueMap.get(value);
        if (result == null) {
            throw new IllegalArgumentException("No element matches " + value);
        }
        return result;
    }

}
