package cn.edu.fudan.issueservice.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SearchUtilTest {

    @Test
    public void   dichotomy(){


        String[] strings = new String[10];
        strings[0]="aa";
        strings[1]="ab";
        strings[2]="ac";
        strings[3]="ad";
        strings[4]="ae";
        strings[5]="af";
        strings[6]="ag";
        strings[7]="ah";
        strings[8]="ai";
        strings[9]="aj";
        String search = "aj";

        int index = SearchUtil.dichotomy(strings,search);
        System.out.println(index);
    }
}
