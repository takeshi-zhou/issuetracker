package cn.edu.fudan.measureservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.edu.fudan.measureservice.mapper")
public class MeasureServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeasureServiceApplication.class, args);
    }

}
