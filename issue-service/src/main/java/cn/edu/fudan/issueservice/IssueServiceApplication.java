package cn.edu.fudan.issueservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@SpringBootApplication
@PropertySource("classpath:conf.properties")
@MapperScan("cn.edu.fudan.issueservice.mapper")
@EnableTransactionManagement//开启事务支持
public class IssueServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IssueServiceApplication.class, args);
	}
}
