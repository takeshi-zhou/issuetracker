package cn.edu.fudan.commitservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:conf.properties")
@MapperScan("cn.edu.fudan.commitservice.mapper")
public class CommitServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommitServiceApplication.class, args);
	}
}
