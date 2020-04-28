package cn.edu.fudan.cloneservice.domain;

/**
 * @author zyh
 * @date 2020/4/28
 */
public class CloneMeasure {

    private int increasedCloneLines;
    private int selfIncreasedCloneLines;
    private double increasedCloneLinesRate;

    public int getIncreasedCloneLines() {
        return increasedCloneLines;
    }

    public void setIncreasedCloneLines(int increasedCloneLines) {
        this.increasedCloneLines = increasedCloneLines;
    }

    public int getSelfIncreasedCloneLines() {
        return selfIncreasedCloneLines;
    }

    public void setSelfIncreasedCloneLines(int selfIncreasedCloneLines) {
        this.selfIncreasedCloneLines = selfIncreasedCloneLines;
    }

    public double getIncreasedCloneLinesRate() {
        return increasedCloneLinesRate;
    }

    public void setIncreasedCloneLinesRate(double increasedCloneLinesRate) {
        this.increasedCloneLinesRate = increasedCloneLinesRate;
    }
}
