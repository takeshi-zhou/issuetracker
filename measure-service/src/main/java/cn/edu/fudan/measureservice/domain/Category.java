package cn.edu.fudan.measureservice.domain;

public enum Category {
    BUG(1,"bug"),CLONE(2,"clone"),SONAR(3,"sonar");

    private int code;
    private String msg;

    private Category(int ordinal, String name)
    {
        this.code = ordinal;		this.msg = name;
    }
    public int getCode()
    {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
