package cn.edu.fudan.scanscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:conf.properties")
public class ScanSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScanSchedulerApplication.class, args);
	}
}
