package cn.edu.fudan.issueservice.domain;

public enum IssueTypeEnum {

    /**
     * BUG,CODE_SMELL,SECURITY_HOTSPOT,VULNERABILITY  属于sonar issue type
     *
     * MALICIOUS_CODE_VULNERABILITY,DODGY_CODE,SECURITY,INTERNATIONALIZATION,CORRECTNESS,BAD_PRACTICE,PERFORMANCE,
     * MULTITHREADED_CORRECTNESS,EXPERIMENTAL   属于 findbugs issue type
     */

    BUG("Bug"),
    CODE_SMELL("Code smell"),
    SECURITY_HOTSPOT("Security hotspot"),
    VULNERABILITY("Vulnerability"),

    MALICIOUS_CODE_VULNERABILITY("Malicious code vulnerability"),
    DODGY_CODE("Dodgy code"),
    SECURITY("Security"),
    INTERNATIONALIZATION("Internationalization"),
    CORRECTNESS("Correctness"),
    BAD_PRACTICE("Bad practice"),
    PERFORMANCE("Performance"),
    MULTITHREADED_CORRECTNESS("Multithreaded correctness"),
    EXPERIMENTAL("Experimental");




    private String name;
    IssueTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static IssueTypeEnum getIssueTypeEnum(String name){
        for(IssueTypeEnum issueTypeEnum : IssueTypeEnum.values()){
            if(issueTypeEnum.getName().equals(name)){
                return issueTypeEnum;
            }
        }
        return null;
    }
}
