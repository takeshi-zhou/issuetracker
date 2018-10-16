package cn.edu.fudan.scanschedule.config;

import cn.edu.fudan.scanschedule.interceptor.AuthTokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Bean
    public AuthTokenInterceptor authTokenInterceptor(){
        return new AuthTokenInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        // 多个拦截器组成一个拦截器链
        // addPathPatterns 用于添加拦截规则
        // excludePathPatterns 用户排除拦截
        List<String> urlPatterns = new ArrayList<>();
        urlPatterns.add("/scan/**");

        registry.addInterceptor(authTokenInterceptor()).addPathPatterns(urlPatterns);
    }

}
