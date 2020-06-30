package cn.edu.fudan.measureservice.portrait;


import lombok.*;

/**
 * description: 开发人员效率 某段时间内完成的工作量
 *
 * @author fancying
 * create: 2020-05-18 21:40
 **/
@AllArgsConstructor
@NoArgsConstructor
public class Efficiency implements Formula{


    // 下面是基础数据 工作量

    @Setter
    int developerCommitCount;
    @Setter
    int totalCommitCount;
    @Setter
    int developerLOC;
    @Setter
    int totalLOC;
    @Setter
    int developerAddStatement;
    @Setter
    int totalAddStatement;
    @Setter
    int developerDelStatement;
    @Setter
    int totalDelStatement;
    @Setter
    int developerValidLine;
    @Setter
    int totalValidLine;
    @Setter
    int developerNumber;


    // 以下各个评分由具体的公式计算出来,在getter方法中实现

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


    private double level = 0;


    /**
     * todo 具体的计算方式
     * @return
     */
    @Override
    public double cal() {
        double efficiencyValue = 0.25*commitFrequency + 0.25*workLoad + 0.25*newLogicLine + 0.25*delLogicLine;

        if (efficiencyValue >= 0 && efficiencyValue <= 0.5/developerNumber){
            return 1;
        }
        if (efficiencyValue > 0.5/developerNumber && efficiencyValue <= 0.67/developerNumber){
            return 2;
        }
        if (efficiencyValue > 0.67/developerNumber && efficiencyValue <= 1.0/developerNumber){
            return 3;
        }
        if (efficiencyValue > 1.0/developerNumber && efficiencyValue <= 1.5/developerNumber){
            return 4;
        }
        if (efficiencyValue > 1.5/developerNumber){
            return 5;
        }
        return 0;
    }


    /**
     * getter
     */

    public double getLevel(){
        if (level != 0) {
            return level;
        }
        level = cal();

        return level;
    }

    public double getCommitFrequency() {
        if (!((Double)defaultScore).equals(commitFrequency)) {
            return commitFrequency;
        }
        //  具体的计算方式
        commitFrequency = (double)developerCommitCount / totalCommitCount;

        return commitFrequency;
    }

    public double getWorkLoad() {
        if (!((Double)defaultScore).equals(workLoad)) {
            return workLoad;
        }
        // 具体的计算方式
        workLoad = (double)developerLOC / totalLOC;
        return workLoad;
    }

    public double getNewLogicLine() {
        if (!((Double)defaultScore).equals(newLogicLine)) {
            return newLogicLine;
        }
        //  具体的计算方式
        newLogicLine = developerAddStatement*(1.0)/totalAddStatement;
        return newLogicLine;
    }

    public double getDelLogicLine() {
        if (!((Double)defaultScore).equals(delLogicLine)) {
            return delLogicLine;
        }
        // 具体的计算方式
        delLogicLine = (totalDelStatement == 0) ? -1 : developerDelStatement*(1.0)/totalDelStatement;
        return delLogicLine;
    }

    public double getValidStatement() {
        if (!((Double)defaultScore).equals(validStatement)) {
            return validStatement;
        }
        //具体的计算方式
        validStatement = (totalValidLine == 0) ? -1 : developerValidLine*(1.0)/totalValidLine;
        return validStatement;
    }

}