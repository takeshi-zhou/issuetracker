package cn.edu.fudan.issueservice;

import cn.edu.fudan.issueservice.scheduler.QuartzScheduler;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author WZY
 * @version 1.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {

    @Value("${inner.service.path}")
    private String innerServicePath;
    @Value("${inner.header.key}")
    private String headerKey;
    @Value("${inner.header.value}")
    private String headerValue;

    @Autowired
    private QuartzScheduler quartzScheduler;

    class User{
        String type;
        String name;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public User(String type, String name) {
            this.type = type;
            this.name = name;
        }
    }


    @org.junit.Test
    public void test(){
        List<User> list=new ArrayList<>();
        list.add(new User("1","qqqqq"));
        list.add(new User("1","wwwww"));
        list.add(new User("2","eeeee"));
        list.add(new User("2","rrrrr"));
        list.add(new User("3","ttttt"));
        list.add(new User("3","yyyyy"));

       Map<String,List<User>> map= list.stream().collect(Collectors.groupingBy(User::getType));
        System.out.println(map);

    }


}
