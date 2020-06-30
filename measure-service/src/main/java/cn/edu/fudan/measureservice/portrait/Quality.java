package cn.edu.fudan.measureservice.portrait;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * description:
 *
 * @author fancying
 * create: 2020-05-18 21:40
 **/
@NoArgsConstructor
@AllArgsConstructor
public class Quality implements Formula{
    // 基础数据
    @Setter
    private int developerStandardIssueCount;
    @Setter
    private int developerSecurityIssueCount;
    @Setter
    private int totalIssueCount;
    @Setter
    private int developerNewIssueCount;
    @Setter
    private int totalNewIssueCount;
    @Setter
    private int totalLOC;

    // 以下各个评分由具体的公式计算出来,在getter方法中实现

    private static double defaultScore = 0;

    private double standard = defaultScore;
    private double security = defaultScore;
    private double issueRate = defaultScore;
    private double issueDensity = defaultScore;

    private double level = 0;



    @Override
    public double cal() {
        level = 0.25*standard + 0.25*security + 0.25*issueRate + 0.25*issueDensity;
        if (level >= 0 && level <= 0.3){
            return 5;
        }
        if (level > 0.3 && level <= 0.5){
            return 4;
        }
        if (level > 0.5 && level <= 0.7){
            return 3;
        }
        if (level > 0.7 && level <= 0.85){
            return 2;
        }
        if (level > 0.85 && level <= 1){
            return 1;
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

    public double getStandard() {
        if (!((Double)defaultScore).equals(standard)) {
            return standard;
        }
        //  具体的计算方式
        if (totalIssueCount != 0){
            standard = developerStandardIssueCount*(1.0)/totalIssueCount;
            return standard;
        }
        return standard;
    }

    public double getSecurity() {
        if (!((Double)defaultScore).equals(security)) {
            return security;
        }
        //  具体的计算方式
        if (totalIssueCount != 0){
            security = developerSecurityIssueCount*(1.0)/totalIssueCount;
            return security;
        }
        return security;
    }

    public double getIssueRate() {
        if (!((Double)defaultScore).equals(issueRate)) {
            return issueRate;
        }
        //  具体的计算方式
        if (totalIssueCount != 0){
            issueRate = developerNewIssueCount*(1.0)/totalNewIssueCount;
            return issueRate;
        }
        return issueRate;
    }

    public double getIssueDensity() {
        if (!((Double)defaultScore).equals(issueDensity)) {
            return issueDensity;
        }
        //  具体的计算方式
        if (totalLOC != 0){
            issueDensity = developerNewIssueCount*(1.0)/totalLOC;
            return issueDensity;
        }
        return issueDensity;
    }



}