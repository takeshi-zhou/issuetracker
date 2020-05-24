package cn.edu.fudan.measureservice.portrait;

/**
 * description:
 *
 * @author fancying
 * create: 2020-05-18 21:41
 **/
public class Competence {

    private double nonRepetitiveCodeRate;
    private double nonSelfRepetitiveCodeRate;
    private double focusRange;
    private double oldCodeModification;
    private double eliminateDuplicateCodeRate;
    private double changedCodeMAXAge;
    private double deletedCodeMAXAge;

    public Competence() {
    }

    public double getNonRepetitiveCodeRate() {
        return nonRepetitiveCodeRate;
    }

    public void setNonRepetitiveCodeRate(double nonRepetitiveCodeRate) {
        this.nonRepetitiveCodeRate = nonRepetitiveCodeRate;
    }

    public double getNonSelfRepetitiveCodeRate() {
        return nonSelfRepetitiveCodeRate;
    }

    public void setNonSelfRepetitiveCodeRate(double nonSelfRepetitiveCodeRate) {
        this.nonSelfRepetitiveCodeRate = nonSelfRepetitiveCodeRate;
    }

    public double getFocusRange() {
        return focusRange;
    }

    public void setFocusRange(double focusRange) {
        this.focusRange = focusRange;
    }

    public double getOldCodeModification() {
        return oldCodeModification;
    }

    public void setOldCodeModification(double oldCodeModification) {
        this.oldCodeModification = oldCodeModification;
    }

    public double getEliminateDuplicateCodeRate() {
        return eliminateDuplicateCodeRate;
    }

    public void setEliminateDuplicateCodeRate(double eliminateDuplicateCodeRate) {
        this.eliminateDuplicateCodeRate = eliminateDuplicateCodeRate;
    }

    public double getChangedCodeMAXAge() {
        return changedCodeMAXAge;
    }

    public void setChangedCodeMAXAge(double changedCodeMAXAge) {
        this.changedCodeMAXAge = changedCodeMAXAge;
    }

    public double getDeletedCodeMAXAge() {
        return deletedCodeMAXAge;
    }

    public void setDeletedCodeMAXAge(double deletedCodeMAXAge) {
        this.deletedCodeMAXAge = deletedCodeMAXAge;
    }
}