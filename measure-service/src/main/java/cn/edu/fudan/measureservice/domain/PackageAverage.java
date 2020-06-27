package cn.edu.fudan.measureservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageAverage {

    private double classes;
    private double functions;
    private double ncss;
    private double javaDocs;

}
