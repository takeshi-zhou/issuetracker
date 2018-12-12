package cn.edu.fudan.projectmanager;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@MapperScan("cn.edu.fudan.projectmanager.mapper")
public class ProjectManagerApplication {


    public static void main(String[] args) {
        SpringApplication.run(ProjectManagerApplication.class, args);
    }
}
