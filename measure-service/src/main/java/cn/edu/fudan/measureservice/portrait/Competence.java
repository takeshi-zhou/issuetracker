package cn.edu.fudan.measureservice.portrait;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * description:
 *
 * @author fancying
 * create: 2020-05-18 21:41
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Competence implements Formula{


    // 基础数据
    @Setter
    private int developerAddStatement;
    @Setter
    private int totalAddStatement;
    @Setter
    private int developerAddLine;
    @Setter
    private int developerValidLine;
    @Setter
    private int totalValidLine;
    @Setter
    private int increasedCloneLines;
    @Setter
    private int selfIncreasedCloneLines;
    @Setter
    private int eliminateCloneLines;
    @Setter
    private int allEliminateCloneLines;
    @Setter
    private int totalChangedFile;
    @Setter
    private int developerFocusFile;
    @Setter
    private int changedCodeAVGAge;
    @Setter
    private int changedCodeMAXAge;
    @Setter
    private int deletedCodeAVGAge;
    @Setter
    private int deletedCodeMAXAge;
    @Setter
    private int repoAge;

    // 以下各个评分由具体的公式计算出来,在getter方法中实现

    private static double defaultScore = 1;


    private double nonRepetitiveCodeRate = defaultScore;
    private double nonSelfRepetitiveCodeRate = defaultScore;
    private double focusRange = defaultScore;
    private double eliminateDuplicateCodeRate = defaultScore;
    private double oldCodeModification = defaultScore;
    private double surviveRate = defaultScore;
    private double level = 0;

    @Override
    public double cal() {
        level = 0.5*surviveRate + 0.2*nonRepetitiveCodeRate + 0.3*(developerAddStatement*1.0/totalAddStatement);
        if (level >=0 && level <= 0.9){
            return 1;
        }
        if (level >0.9 && level <= 0.93){
            return 2;
        }
        if (level >0.93 && level <= 0.96){
            return 3;
        }
        if (level >0.96 && level <= 0.98){
            return 4;
        }
        if (level >0.98 && level <= 1){
            return 5;
        }
        return 0;
    }

    public double getNonRepetitiveCodeRate() {
        if (!((Double)defaultScore).equals(nonRepetitiveCodeRate)) {
            return nonRepetitiveCodeRate;
        }
        //  具体的计算方式
        if (developerAddLine != 0){
            nonRepetitiveCodeRate = (developerAddLine - increasedCloneLines)*(1.0)/developerAddLine;
            return nonRepetitiveCodeRate;
        }
        return nonRepetitiveCodeRate;
    }

    public double getNonSelfRepetitiveCodeRate() {
        if (!((Double)defaultScore).equals(nonSelfRepetitiveCodeRate)) {
            return nonSelfRepetitiveCodeRate;
        }
        //  具体的计算方式
        if (developerAddLine != 0){
            nonSelfRepetitiveCodeRate = (developerAddLine - selfIncreasedCloneLines)*(1.0)/developerAddLine;
            return nonSelfRepetitiveCodeRate;
        }
        return nonSelfRepetitiveCodeRate;
    }

    public double getFocusRange() {
        if (!((Double)defaultScore).equals(focusRange)) {
            return focusRange;
        }
        //  具体的计算方式
        if (totalChangedFile != 0){
            focusRange = (developerFocusFile)*(1.0)/totalChangedFile;
            return focusRange;
        }
        return focusRange;
    }

    public double getOldCodeModification() {
        if (!((Double)defaultScore).equals(oldCodeModification)) {
            return oldCodeModification;
        }
        //  具体的计算方式
        if (repoAge != 0){
            oldCodeModification = (changedCodeAVGAge + deletedCodeAVGAge)*(1.0)/repoAge;
            return oldCodeModification;
        }
        return oldCodeModification;
    }

    public double getEliminateDuplicateCodeRate() {
        if (!((Double)defaultScore).equals(eliminateDuplicateCodeRate)) {
            return eliminateDuplicateCodeRate;
        }
        //  具体的计算方式
        if (allEliminateCloneLines != 0){
            eliminateDuplicateCodeRate = (eliminateCloneLines)*(1.0)/allEliminateCloneLines;
            return eliminateDuplicateCodeRate;
        }
        return eliminateDuplicateCodeRate;
    }

    public double getSurviveRate() {
        if (defaultScore != surviveRate) {
            return surviveRate;
        }
        //  具体的计算方式
        if (totalValidLine != 0){
            surviveRate = (developerValidLine)*(1.0)/totalValidLine;
            return surviveRate;
        }
        return surviveRate;
    }

    public double getLevel() {
        if (level != 0) {
            return level;
        }
        level = cal();
        return level;
    }






}