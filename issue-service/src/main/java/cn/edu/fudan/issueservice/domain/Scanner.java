package cn.edu.fudan.issueservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Scanner {
    //findbugs 工具
    FINDBUGS("bug"),
    //实验室克隆工具
    CLONE("clone"),
    //sonar qube 工具
    SONAR("sonar");
    private String type;
}
