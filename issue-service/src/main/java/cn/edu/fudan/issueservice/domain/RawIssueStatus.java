package cn.edu.fudan.issueservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RawIssueStatus {
    //issue 的 第一条raw issue 插入时的状态
    ADD("add"),
    //raw issue 对比前一条raw issue location 发生改变
    CHANGED("changed"),
    //这个issue 在这个commit 被消除
    SOLVED("solved");
    private String type;

    public static RawIssueStatus getStatusByName(String name){
        for(RawIssueStatus status : RawIssueStatus.values()){
            if(status.getType().equals(name)){
                return status;
            }
        }
        return null;
    }
}
