package cn.edu.fudan.issueservice.domain;
import	java.util.ArrayList;
import java.util.List;

import cn.edu.fudan.issueservice.util.TestDataMaker;
import org.junit.Test;

public class LocationTest {

    @Test
    public void LocationEqualTest(){
        TestDataMaker testDataMaker = new TestDataMaker();

        Location a = testDataMaker.locationMaker1();
        Location b = testDataMaker.locationMaker1();

        System.out.println(a.equals(b));
    }


    @Test
    public void LocationContainsTest(){
        TestDataMaker testDataMaker = new TestDataMaker();

        Location a = testDataMaker.locationMaker1();
        Location b = testDataMaker.locationMaker1();
        List aa = new ArrayList();
        aa.add(a);
        List bb = new ArrayList();
        bb.add(b);
        System.out.println(aa.contains(b));
    }
}
