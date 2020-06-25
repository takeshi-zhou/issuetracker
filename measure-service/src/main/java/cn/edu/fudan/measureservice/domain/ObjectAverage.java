package cn.edu.fudan.measureservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObjectAverage {
    private double ncss;
    private double functions;
    private double classes;
    private double javaDocs;
    private double javaDocsLines;
    private double singleCommentLines;
    private double implementationCommentLines;

}
