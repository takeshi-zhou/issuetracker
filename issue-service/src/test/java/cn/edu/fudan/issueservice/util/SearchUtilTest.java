package cn.edu.fudan.issueservice.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SearchUtilTest {

    @Test
    public void   dichotomy(){
        String[] strings = new String[10];
        strings[0]="AW7QE6f1bHwuxD0PwnLx";
        strings[1]="AW7QE6f1bHwuxD0PwnLy";
        strings[2]="AW7QE6flbHwuxD0PwnLw";
        strings[3]="AW7QE6g-bHwuxD0PwnL_";
        strings[4]="AW7QE6g2bHwuxD0PwnL-";
        strings[5]="AW7QE6gabHwuxD0PwnL4";
        strings[6]="AW7QE6gEbHwuxD0PwnLz";
        strings[7]="AW7QE6gmbHwuxD0PwnL5";
        strings[8]="AW7QE6gmbHwuxD0PwnL6";
        strings[9]="AW7QE6gNbHwuxD0PwnL0";
        String search = "AW7QE6f1bHwuxD0PwnLx";
        int index = SearchUtil.dichotomy(strings,search);
        System.out.println(index);
    }
}
