package cn.edu.fudan.cloneservice.bean;

import java.util.List;

public class GroupInfo {
    public String name;
    public List<MethodInfo> children;

    public GroupInfo(String name, List<MethodInfo> children){
        this.name = name;
        this.children = children;
    }
}
