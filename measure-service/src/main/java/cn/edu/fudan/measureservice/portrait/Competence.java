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
    private int developerNumber;
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
    private static double defaultLevel = 0;


    private double nonRepetitiveCodeRate = defaultScore;
    private double nonSelfRepetitiveCodeRate = defaultScore;
    private double focusRange = defaultScore;
    private double eliminateDuplicateCodeRate = defaultScore;
    private double oldCodeModification = defaultScore;
    private double surviveRate = defaultScore;
    private double nonRepetitiveCodeRateLevel = defaultLevel;
    private double nonSelfRepetitiveCodeRateLevel = defaultLevel;
    private double eliminateDuplicateCodeRateLevel = defaultLevel;
    private double oldCodeModificationLevel = defaultLevel;
    private double level = defaultLevel;

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

    public double getNonRepetitiveCodeRateLevel() {
        if (defaultLevel != nonRepetitiveCodeRateLevel) {
            return nonRepetitiveCodeRateLevel;
        }
        //  具体的计算方式
        if (nonRepetitiveCodeRate >= 0 && nonRepetitiveCodeRate < 0.85){
            nonRepetitiveCodeRateLevel = 1;
            return nonRepetitiveCodeRateLevel;
        }
        if (nonRepetitiveCodeRate >= 0.85 && nonRepetitiveCodeRate < 0.9){
            nonRepetitiveCodeRateLevel = 2;
            return nonRepetitiveCodeRateLevel;
        }
        if (nonRepetitiveCodeRate >= 0.9 && nonRepetitiveCodeRate < 0.93){
            nonRepetitiveCodeRateLevel = 3;
            return nonRepetitiveCodeRateLevel;
        }
        if (nonRepetitiveCodeRate >= 0.93 && nonRepetitiveCodeRate < 0.96){
            nonRepetitiveCodeRateLevel = 4;
            return nonRepetitiveCodeRateLevel;
        }
        if (nonRepetitiveCodeRate >= 0.96 && nonRepetitiveCodeRate <= 1){
            nonRepetitiveCodeRateLevel = 5;
            return nonRepetitiveCodeRateLevel;
        }
        return nonRepetitiveCodeRateLevel;
    }

    public double getNonSelfRepetitiveCodeRateLevel() {
        if (defaultLevel != nonSelfRepetitiveCodeRateLevel) {
            return nonSelfRepetitiveCodeRateLevel;
        }
        //  具体的计算方式
        if (nonSelfRepetitiveCodeRate >= 0 && nonSelfRepetitiveCodeRate < 0.85){
            nonSelfRepetitiveCodeRateLevel = 1;
            return nonSelfRepetitiveCodeRateLevel;
        }
        if (nonSelfRepetitiveCodeRate >= 0.85 && nonSelfRepetitiveCodeRate < 0.9){
            nonSelfRepetitiveCodeRateLevel = 2;
            return nonSelfRepetitiveCodeRateLevel;
        }
        if (nonSelfRepetitiveCodeRate >= 0.9 && nonSelfRepetitiveCodeRate < 0.93){
            nonSelfRepetitiveCodeRateLevel = 3;
            return nonSelfRepetitiveCodeRateLevel;
        }
        if (nonSelfRepetitiveCodeRate >= 0.93 && nonSelfRepetitiveCodeRate < 0.96){
            nonSelfRepetitiveCodeRateLevel = 4;
            return nonSelfRepetitiveCodeRateLevel;
        }
        if (nonSelfRepetitiveCodeRate >= 0.96 && nonSelfRepetitiveCodeRate <= 1){
            nonSelfRepetitiveCodeRateLevel = 5;
            return nonSelfRepetitiveCodeRateLevel;
        }
        return nonSelfRepetitiveCodeRateLevel;
    }

    public double getEliminateDuplicateCodeRateLevel() {
        if (defaultLevel != eliminateDuplicateCodeRateLevel) {
            return eliminateDuplicateCodeRateLevel;
        }
        //具体的计算方式
        if (eliminateDuplicateCodeRate >= 0 && eliminateDuplicateCodeRate <= 0.5/developerNumber){
            eliminateDuplicateCodeRateLevel = 1;
            return eliminateDuplicateCodeRateLevel;
        }
        if (eliminateDuplicateCodeRate > 0.5/developerNumber && eliminateDuplicateCodeRate <= 0.67/developerNumber){
            eliminateDuplicateCodeRateLevel = 2;
            return eliminateDuplicateCodeRateLevel;
        }
        if (eliminateDuplicateCodeRate > 0.67/developerNumber && eliminateDuplicateCodeRate <= 1.0/developerNumber){
            eliminateDuplicateCodeRateLevel = 3;
            return eliminateDuplicateCodeRateLevel;
        }
        if (eliminateDuplicateCodeRate > 1.0/developerNumber && eliminateDuplicateCodeRate <= 1.5/developerNumber){
            eliminateDuplicateCodeRateLevel = 4;
            return eliminateDuplicateCodeRateLevel;
        }
        if (eliminateDuplicateCodeRate > 1.5/developerNumber){
            eliminateDuplicateCodeRateLevel = 5;
            return eliminateDuplicateCodeRateLevel;
        }
        return eliminateDuplicateCodeRateLevel;
    }

    public double getOldCodeModificationLevel() {
        if (defaultLevel != oldCodeModificationLevel) {
            return oldCodeModificationLevel;
        }
        //  具体的计算方式
        if (oldCodeModification >= 0 && oldCodeModification < 0.05){
            oldCodeModificationLevel = 1;
            return oldCodeModificationLevel;
        }
        if (oldCodeModification >= 0.05 && oldCodeModification < 0.08){
            oldCodeModificationLevel = 2;
            return oldCodeModificationLevel;
        }
        if (oldCodeModification >= 0.08 && oldCodeModification < 0.2){
            oldCodeModificationLevel = 3;
            return oldCodeModificationLevel;
        }
        if (oldCodeModification >= 0.2 && oldCodeModification < 0.4){
            oldCodeModificationLevel = 4;
            return oldCodeModificationLevel;
        }
        if (oldCodeModification >= 0.4){
            oldCodeModificationLevel = 5;
            return oldCodeModificationLevel;
        }
        return oldCodeModificationLevel;
    }

    public double getLevel() {
        if (level != defaultLevel) {
            return level;
        }
        level = cal();
        return level;
    }






}