package cn.edu.fudan.measureservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fancying
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Total {
    private int files;
    private int classes;
    private int functions;
    private int ncss;
    private int javaDocs;
    private int javaDocsLines;
    private int singleCommentLines;
    private int multiCommentLines;
}
