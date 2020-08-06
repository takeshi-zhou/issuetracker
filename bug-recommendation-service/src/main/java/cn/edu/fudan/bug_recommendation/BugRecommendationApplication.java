package cn.edu.fudan.bug_recommendation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.edu.fudan.bug_recommendation.mapper")
public class BugRecommendationApplication {

    public static void main(String[] args) {
        SpringApplication.run(BugRecommendationApplication.class, args);
    }

}
