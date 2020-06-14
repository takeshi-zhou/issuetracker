package cn.edu.fudan.cloneservice.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ForkJoinPool;

/**
 * @author zyh
 * @date 2020/6/12
 */
@Configuration
public class ForkJoinPoolConfig {

    @Bean
    public ForkJoinPool getForkJoinPool(){
        return new ForkJoinPool();
    }
}
