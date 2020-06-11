package cn.edu.fudan.measureservice.domain.core;

import cn.edu.fudan.measureservice.portrait.BaseMetrics;
import lombok.*;

/**
 * description:
 *
 * @author fancying
 * create: 2020-06-10 23:56
 **/
@Getter
@Setter
@AllArgsConstructor
public class FileMeasure extends BaseMetrics {

    String repoId;
    String commitId;
    int filePath;

    int ccn;
    int addLine;
    int deleteLine;
    int totalLine;

    public FileMeasure() {
        super();
    }

}