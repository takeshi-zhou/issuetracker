package cn.edu.fudan.cloneservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@PropertySource("classpath:conf.properties")
@MapperScan("cn.edu.fudan.cloneservice.mapper")
@EnableTransactionManagement//开启事务支持
@EnableAsync//开启异步调用的支持
public class CloneServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloneServiceApplication.class, args);
    }
}
