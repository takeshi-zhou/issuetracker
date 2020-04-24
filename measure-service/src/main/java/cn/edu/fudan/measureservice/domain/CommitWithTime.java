package cn.edu.fudan.measureservice.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommitWithTime {
    private String repoId;
    private String commitId;
    private String commitTime;
    private String developerName;
    private String developerEmail;
}
