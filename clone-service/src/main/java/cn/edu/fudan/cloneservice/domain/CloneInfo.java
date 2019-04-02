package cn.edu.fudan.cloneservice.domain;

public class CloneInfo {
    private String uuid;
    private int start_line;
    private int end_line;
    private String file_path;
    private String class_name;
    private String method_name;

    public CloneInfo(String uuid, int start_line, int end_line, String file_path, String class_name, String method_name) {
        this.uuid = uuid;
        this.start_line = start_line;
        this.end_line = end_line;
        this.file_path = file_path;
        this.class_name = class_name;
        this.method_name = method_name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
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
}
