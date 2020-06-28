package cn.edu.fudan.measureservice.portrait;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * description:
 *
 * @author fancying
 * create: 2020-05-18 21:40
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quality {

    private double standard;
    private double security;
    private double issueRate;
    private double issueDensity;

}