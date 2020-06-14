package cn.edu.fudan.cloneservice.config;

import cn.edu.fudan.cloneservice.thread.MultiThreadingExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zyh
 * @date 2020/6/4
 */
@Configuration
public class MultiThreadExtractorConfig {

    @Value("${core.pool.size}")
    private int corePoolSize;

    @Bean
    public MultiThreadingExtractor multiThreadingExtractor(){

        return new MultiThreadingExtractor(corePoolSize);
    }


}
