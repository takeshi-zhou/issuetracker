package cn.edu.fudan.measureservice;


import java.io.IOException;
import java.math.BigDecimal;

public class Test {

    public static void main(String[] args) throws IOException {
        BigDecimal b1=new BigDecimal("1.83");
        BigDecimal b2=new BigDecimal("2.42");
        System.out.println(b1.subtract(b2).doubleValue());
    }
}
