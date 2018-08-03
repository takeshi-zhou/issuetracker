package cn.edu.fudan.commitservice.config;

import cn.edu.fudan.commitservice.interceptor.AuthTokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WZY
 * @version 1.0
 **/
@Configuration
//注册拦截器时不要去继承WebMvcConfigurationSupport，这会把WebMvcAutoConfiguration的配置都覆盖掉，所以在全局配置文件里配置的日期格式化无效
//所以这里直接实现WebMvcConfigurer接口来注册拦截器
public class MvcConfig implements WebMvcConfigurer {

    //将拦截器配置为bean
    @Bean
    public AuthTokenInterceptor authTokenInterceptor(){
        return new AuthTokenInterceptor();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> urlPatterns=new ArrayList<>();
        //添加拦截的URL
        urlPatterns.add("/Commit/commitList");

        registry.addInterceptor(authTokenInterceptor()).addPathPatterns(urlPatterns);
    }

}
