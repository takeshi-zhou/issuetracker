package cn.edu.fudan.measureservice.service;

import cn.edu.fudan.measureservice.MeasureServiceApplicationTests;
import cn.edu.fudan.measureservice.domain.CommitBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MeasureServiceTest extends MeasureServiceApplicationTests {

    @Autowired
    MeasureService measureService;


    @Test
    public void getCodeChangesByDurationAndDeveloperName(){
        Object commitBase = measureService.getCodeChangesByDurationAndDeveloperName("linwangzai","2019-10-10","2019-12-16","ec15d79e36e14dd258cfff3d48b73d35","bug","4e21a81e-1efb-11ea-9fbe-11bdb9e75ae6");
        System.out.println(111);
    }
}
