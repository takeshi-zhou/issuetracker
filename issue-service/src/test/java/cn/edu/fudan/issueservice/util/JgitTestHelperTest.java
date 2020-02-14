package cn.edu.fudan.issueservice.util;

import java.util.Optional;

/**
 * @ProjectName: issueTracker
 * @Package: cn.edu.fudan.issueservice.util
 * @ClassName: JgitTestHelperTest
 * @Description:
 * @Author: bruce
 * @CreateDate: 2020/2/14 13:59
 * @Version: 1.0
 */
public class JgitTestHelperTest {
    public static void main(String[] args) {
        String nummName = "asd";
        String opt = Optional.ofNullable(nummName).orElse("john");
        System.out.println(opt);
    }
}
