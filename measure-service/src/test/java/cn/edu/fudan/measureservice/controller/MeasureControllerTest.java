package cn.edu.fudan.measureservice.controller;

import cn.edu.fudan.measureservice.MeasureServiceApplicationTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;

@PrepareForTest({})
@WebAppConfiguration
public class MeasureControllerTest extends MeasureServiceApplicationTests {

    @Autowired
    MeasureController measureController;

    public void getCodeChangesByDurationAndDeveloperNameTest(){

    }


}
