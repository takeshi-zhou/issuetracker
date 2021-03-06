package cn.edu.fudan.measureservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author fancying
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseBean implements Serializable {

    private int code;

    private String msg;

    private java.lang.Object data;

}
