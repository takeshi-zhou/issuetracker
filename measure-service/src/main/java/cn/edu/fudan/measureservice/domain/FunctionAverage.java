package cn.edu.fudan.measureservice.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunctionAverage {
    private double ncss;
    private double ccn;
    private double javaDocs;
}
