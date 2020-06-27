package cn.edu.fudan.measureservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OObject {
    private String name;
    private int ncss;
    private int functions;
    private int classes;
    private int javaDocs;
    private int javaDocsLines;
    private int singleCommentLines;
    private int implementationCommentLines;
}
