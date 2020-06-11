package cn.edu.fudan.measureservice.domain;



import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author WZY
 * @version 1.0
 **/
@Data
@Builder
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

}
