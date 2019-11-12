package cn.edu.fudan.measureservice.domain;



import java.io.Serializable;

/**
 * @author WZY
 * @version 1.0
 **/
public class ResponseBean implements Serializable {

    private int code;

    private String msg;

    private java.lang.Object data;

    public ResponseBean() {
    }

    public ResponseBean(int code, String msg, java.lang.Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
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

    public java.lang.Object getData() {
        return data;
    }

    public void setData(java.lang.Object data) {
        this.data = data;
    }
}
