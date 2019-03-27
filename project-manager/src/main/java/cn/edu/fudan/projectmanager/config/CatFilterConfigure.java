package cn.edu.fudan.projectmanager.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.dianping.cat.servlet.CatFilter;

/**
 * @author: shengwang.lin
 * @date: 2019-03-20 19:00
 * @description: 添加过滤器 CatFilter
 **/
@Configuration
public class CatFilterConfigure {
    @Bean
    public FilterRegistrationBean catFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new CatFilter());
        registration.addUrlPatterns("/*");
        registration.setName("cat-filter");
        registration.setOrder(1);
        return registration;
    }
}