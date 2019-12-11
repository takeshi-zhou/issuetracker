package cn.edu.fudan.issueservice.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SearchUtilTest {

    @Test
    public void   dichotomy(){
        String[] strings = new String[8];
        strings[0]="Lx";
        strings[1]="Ly";
        strings[2]="Lw";
        strings[3]="MN";
        strings[4]="Mn";
        strings[5]="NN";
        strings[6]="NNL";
        strings[7]="Nn";

        String search = "Lx";
        int index = SearchUtil.dichotomy(strings,search);
        System.out.println(index);
    }
}
