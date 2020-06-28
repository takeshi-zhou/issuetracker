package cn.edu.fudan.measureservice.portrait;


import lombok.*;

/**
 * description: 开发人员效率 某段时间内完成的工作量
 *
 * @author fancying
 * create: 2020-05-18 21:40
 **/
@Getter
@Setter
@NoArgsConstructor
public class Efficiency implements Formula{


    // 下面是基础数据 工作量

    @Setter
    int commitCount;
    @Setter
    int addLine;
    @Setter
    int deleteLine;
    @Setter
    int addStatement;
    @Setter
    int deleteStatement;


    int intervalDay;
    String duration;

    // 以下各个评分由具体的公式计算出来 分值统一在 1 - 10之间

    private static double defaultScore = -1;
    /**
     * 提交频率评分
     * todo 需要考虑排除节假日计算提交频率
     */
    private double commitFrequency = defaultScore;
    private double workLoad = defaultScore;
    private double newLogicLine = defaultScore;
    private double delLogicLine = defaultScore;
    private double validStatement = defaultScore;


    private double level = defaultScore;


    /**
     * todo 具体的计算方式
     */
    @Override
    public double cal() {
        return 0;
    }


    /**
     * getter
     */

    public double getLevel(){
        if (!((Double)defaultScore).equals(level)) {
            return level;
        }
        level = cal();

        return level;
    }

    public double getCommitFrequency() {
        if (!((Double)defaultScore).equals(commitFrequency)) {
            return commitFrequency;
        }
        // todo 具体的计算方式
        commitFrequency = (double)commitCount / intervalDay;

        return commitFrequency;
    }

    public double getWorkLoad() {
        return workLoad;
    }

    public double getNewLogicLine() {
        return newLogicLine;
    }

    public double getDelLogicLine() {
        return delLogicLine;
    }

    public double getValidStatement() {
        return validStatement;
    }

}