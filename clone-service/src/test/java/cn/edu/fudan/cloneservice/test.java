package cn.edu.fudan.cloneservice;

/**
 * @author zyh
 * @date 2020/5/29
 */
public class test {

    public static void main(String[] args) {
        String s1 = "123+hadoop";
        System.out.println(s1.substring(s1.indexOf("+")+1));
        System.out.println(s1.substring(0, s1.indexOf("+")));
    }
}
