package cn.edu.fudan.cloneservice.bean;

public class CloneInstanceInfo {
    private int groupID;
    private int start_line;
    private int end_line;
    private String class_name;
    private String method_name;

    public CloneInstanceInfo(int groupID, int start_line, int end_line, String class_name, String method_name) {
        this.groupID = groupID;
        this.start_line = start_line;
        this.end_line = end_line;
        this.class_name = class_name;
        this.method_name = method_name;
    }

    public String getPackageName(){
        String div[] = class_name.split("-\\*-");
        if(div.length != 2){
            return "get_package_name_error";
        }else {
            return div[0];
        }
    }
    public String getClassName(){
        String div[] = class_name.split("-\\*-");
        if(div.length != 2){
            return "get_class_name_error";
        }else {
            return div[1];
        }
    }

    public int getStart_line() {
        return start_line;
    }

    public void setStart_line(int start_line) {
        this.start_line = start_line;
    }

    public int getEnd_line() {
        return end_line;
    }

    public void setEnd_line(int end_line) {
        this.end_line = end_line;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getMethod_name() {
        return method_name;
    }

    public void setMethod_name(String method_name) {
        this.method_name = method_name;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }
}
