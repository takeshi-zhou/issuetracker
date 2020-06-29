package cn.edu.fudan.measureservice.portrait;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * description: 记录开发人员整体的画像
 *
 * @author fancying
 * create: 2020-06-28 22:35
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeveloperPortrait {

    private Date firstCommitDate;
    private int totalLine;
    private int dayAverageLine;
    private int totalCommitCount;
    private String developerName;
    private String developerType;


    private List<DeveloperMetrics> developerMetricsList;

    private static double defaultLevel = -1;
    private double level = defaultLevel;
    private double value = defaultLevel;
    private double quality = defaultLevel;
    private double efficiency = defaultLevel;


    public double getLevel() {
        return level;
    }

    public double getValue() {
        return value;
    }

    public double getQuality() {
        return quality;
    }

    public double getEfficiency() {
        return efficiency;
    }

}