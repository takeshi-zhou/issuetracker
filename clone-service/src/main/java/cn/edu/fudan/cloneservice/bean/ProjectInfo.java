package cn.edu.fudan.cloneservice.bean;

import java.util.List;

public class ProjectInfo {
    public String name;
    public List<Object> children;

    public ProjectInfo(String name, List children){
        this.name = name;
        this.children = children;
    }
}
