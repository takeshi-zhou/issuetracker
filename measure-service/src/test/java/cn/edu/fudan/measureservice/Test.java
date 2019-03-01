package cn.edu.fudan.measureservice;


import java.io.IOException;

public class Test {

    public static void main(String[] args) throws IOException {
           Runtime  runtime=Runtime.getRuntime();
           runtime.exec("C:\\Users\\WZY\\Desktop\\javancss-master\\bin\\javancss.bat -all -xml -recursive C:\\wzy\\programme\\intellijWorkSpace\\leet-code > C:\\Users\\WZY\\Desktop\\leet-code.xml");
    }
}
