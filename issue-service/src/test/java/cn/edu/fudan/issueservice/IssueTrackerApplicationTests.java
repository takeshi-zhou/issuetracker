package cn.edu.fudan.issueservice;

import cn.edu.fudan.issueservice.component.RestInterfaceManager;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-develop.properties")
@SpringBootTest
public class IssueTrackerApplicationTests {

    @Autowired
    RestInterfaceManager restInterfaceManager;


    @Test
    public void addTags(){
        List<String> tags = new ArrayList<>();
        tags.add("a890f64d-c485-4259-b9a3-8cb702843145");

        restInterfaceManager.getSolvedIssueIds(tags);
        System.out.println("result");
    }
}
