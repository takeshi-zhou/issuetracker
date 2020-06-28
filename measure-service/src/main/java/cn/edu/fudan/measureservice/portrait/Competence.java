package cn.edu.fudan.measureservice.portrait;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private double nonRepetitiveCodeRate;
    private double nonSelfRepetitiveCodeRate;
    private double focusRange;
    private double oldCodeModification;
    private double eliminateDuplicateCodeRate;
    private double changedCodeMAXAge;
    private double deletedCodeMAXAge;


    private double level;

    @Override
    public double cal() {
        return 0;
    }
}