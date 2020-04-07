package cn.edu.fudan.issueservice.domain;

public enum PriorityEnum {

    /**
     * IGNORE 代表不解决该缺陷
     * OPEN   代表该缺陷正待解决
     * MISINFORMATION   代表该缺陷属于误报，假阳性
     * SOLVED  代表该缺陷已被解决
     * TO_REVIEW  代表该问题需要review是否真的是个缺陷
     */
    LOW("Low"),
    URGENT("Urgent"),
    NORMAL("Normal"),
    HIGH("High"),
    IMMEDIATE("Immediate");

    private String name;
    PriorityEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static PriorityEnum getPriorityEnum(String name){
        for(PriorityEnum priority : PriorityEnum.values()){
            if(priority.getName().equals(name)){
                return priority;
            }
        }
        return null;
    }
}
