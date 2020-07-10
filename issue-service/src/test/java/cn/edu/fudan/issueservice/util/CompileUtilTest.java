package cn.edu.fudan.issueservice.util;

/**
 * description:
 *
 * @author fancying
 * create: 2020-07-02 12:26
 **/
public class CompileUtilTest {
    public static void main(String[] args) {
        CompileUtil compileUtil = new CompileUtil ();
        //compileUtil.mvnHome = "D:\\apache-maven-3.6.1";
        String repoPath = "E:\\Lab\\scanProject\\poms-shared";

        System.out.println (compileUtil.isCompilable (repoPath));

    }

}