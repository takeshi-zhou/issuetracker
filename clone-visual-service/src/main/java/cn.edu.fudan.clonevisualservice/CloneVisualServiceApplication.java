package cn.edu.fudan.clonevisualservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
@SpringBootApplication
@MapperScan(basePackages = {"cn.edu.fudan.clonevisualservice.mapper"})
public class CloneVisualServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloneVisualServiceApplication.class, args);
    }
}
