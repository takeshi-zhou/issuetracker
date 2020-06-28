package cn.edu.fudan.measureservice.portrait;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * description: 开发者指标
 *
 * @author fancying
 * create: 2020-05-18 21:26
 **/
@Data
public class DeveloperMetrics{

    private Date firstCommitDate;
    private int totalLine;

    /**
     * 项目名称 项目名称为 ALL 时 代表的是整体情况
     */
    private String repoName;

    private String developer;
    private Efficiency efficiency;
    private Quality quality;
    private Competence competence;

    private double level = -1;


    public DeveloperMetrics(String developer, Efficiency efficiency, Quality quality, Competence competence) {
        this.developer = developer;
        this.efficiency = efficiency;
        this.quality = quality;
        this.competence = competence;
    }
}