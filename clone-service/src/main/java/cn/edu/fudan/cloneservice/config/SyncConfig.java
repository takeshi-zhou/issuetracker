package cn.edu.fudan.cloneservice.config;

import cn.edu.fudan.cloneservice.lock.CloneMeasureLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zyh
 * @date 2020/6/4
 */
@Configuration
public class SyncConfig {

    @Value("${clone.measure.lock.size}")
    private int cloneMeasureLockSize;

    @Bean
    public CloneMeasureLock cloneMeasureLock(){
        return new CloneMeasureLock(cloneMeasureLockSize);
    }
}
