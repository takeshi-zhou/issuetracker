package cn.edu.fudan.measureservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2 {

    // http://10.141.221.85:8008/swagger-ui.html  原路径
    // http://10.141.221.85:8008/doc.html  优化界面后的路径
    //swagger2 核心配置
    @Bean
    public Docket createRestApi() {

        return new Docket(DocumentationType.SWAGGER_2)    //指定api类型为swagger2
                .apiInfo(apiInfo())                       //用于定义api文档汇总信息
                .select()
                .apis(RequestHandlerSelectors
                        .basePackage("cn.edu.fudan.measureservice.controller")) //指定controller包
                .paths(PathSelectors.any())               //所有controller
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("CodeWisdom 度量服务api接口")   //文档页标题
                .contact(new Contact("FDSELab",
                        "http://10.141.221.85:8888/",
                        "fdseapitest@163.com"))    //联系人信息
                .description("专为CodeWisdom代码大数据平台度量服务提供的api文档")  //描述信息
                .version("1.0.1")       //文档版本号
                .termsOfServiceUrl("http://10.141.221.85:8888/") //网站地址
                .build();
    }

}

