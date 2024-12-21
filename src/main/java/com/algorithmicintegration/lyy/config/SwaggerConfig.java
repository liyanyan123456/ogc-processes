package com.algorithmicintegration.lyy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.awt.datatransfer.StringSelection;


@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {

    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.algorithmicintegration.lyy.Controller"))//指定要扫描的包
                .paths(PathSelectors.regex("/processes_api.*"))// 匹配包含"/process"的路径
                .build()
                ;
    }

    private ApiInfo apiInfo(){

        Contact contact=new Contact("liyanyan", "", "2073820553@qq.com");
        return new ApiInfoBuilder()
                .title("算子提交发布api接口")
                .contact(contact)
                .description(" API Description：用户提交代码及描述性文件")
                .version("1.0.0")
                .build();

    }
}
