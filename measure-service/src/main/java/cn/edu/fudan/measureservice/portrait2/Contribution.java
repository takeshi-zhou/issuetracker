package cn.edu.fudan.measureservice.portrait2;

import cn.edu.fudan.measureservice.portrait.Formula;
import lombok.*;

import java.io.Serializable;

/**
 * description:
 *
 * @author fancying
 * create: 2020-05-18 21:41
 **/
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contribution implements Formula, Serializable {

    // 基础数据
    private int developerLOC;
    private int totalLOC;
    private int developerAddStatement;
    private int totalAddStatement;
    private int developerAddLine;
    private int developerValidLine;//个人存活语句
    private int totalValidLine;//团队存活语句
    private int increasedCloneLines;//个人新增重复代码行数
    private int developerAssignedJiraCount;//个人被分配到的jira任务个数（注意不是次数）
    private int totalAssignedJiraCount;//团队被分配到的jira任务个数（注意不是次数）
    private int developerSolvedJiraCount;//个人解决的jira任务个数（注意不是次数）
    private int totalSolvedJiraCount;//团队解决的jira任务个数（注意不是次数）

    // 以下各个评分由具体的公式计算出来,在getter方法中实现

    private double defaultScore = 1.0;

    private double locContribution = defaultScore;//代码贡献率（个人新增+删除）/（团队新增+删除）
    private double addStatementRate = defaultScore;//个人新增语句/团队新增语句
    private double surviveRate = defaultScore;//个人存活语句数/团队存活语句数
    private double nonRepetitiveCodeRate = defaultScore;//个人非重复代码行数/个人新增代码行数
    private double assignedJiraRate = defaultScore;//个人被分配到的jira任务个数/团队被分配到的jira任务个数
    private double solvedJiraRate = defaultScore;//个人解决的jira任务个数/团队解决的jira任务个数


    public double getLocContribution() {
        if (defaultScore != locContribution) {
            return locContribution;
        }
        //  具体的计算方式
        if (totalLOC != 0){
            locContribution = developerLOC*(1.0)/totalLOC;
            return locContribution;
        }
        return locContribution;
    }

    public double getAddStatementRate() {
        if (defaultScore != addStatementRate) {
            return addStatementRate;
        }
        //  具体的计算方式
        if (totalAddStatement != 0){
            addStatementRate = developerAddStatement*(1.0)/totalAddStatement;
            return addStatementRate;
        }
        return addStatementRate;
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

    public double getNonRepetitiveCodeRate() {
        if (defaultScore != nonRepetitiveCodeRate) {
            return nonRepetitiveCodeRate;
        }
        //  具体的计算方式
        if (developerAddLine != 0){
            nonRepetitiveCodeRate = (developerAddLine - increasedCloneLines)*(1.0)/developerAddLine;
            return nonRepetitiveCodeRate;
        }
        return nonRepetitiveCodeRate;
    }

    public double getAssignedJiraRate() {
        if (defaultScore != assignedJiraRate) {
            return assignedJiraRate;
        }
        //  具体的计算方式
        if (totalAssignedJiraCount != 0){
            assignedJiraRate = developerAssignedJiraCount*(1.0)/totalAssignedJiraCount;
            return assignedJiraRate;
        }
        return assignedJiraRate;
    }

    public double getSolvedJiraRate() {
        if (defaultScore != solvedJiraRate) {
            return solvedJiraRate;
        }
        //  具体的计算方式
        if (totalSolvedJiraCount != 0){
            solvedJiraRate = developerSolvedJiraCount*(1.0)/totalSolvedJiraCount;
            return solvedJiraRate;
        }
        return solvedJiraRate;
    }

    @Override
    public double cal() {
        return 0;
    }

    public int getLevel() {
        return 0;
    }
}
