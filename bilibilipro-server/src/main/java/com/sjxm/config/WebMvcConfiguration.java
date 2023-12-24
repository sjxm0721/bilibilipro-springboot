package com.sjxm.config;

import com.sjxm.interceptor.JwtTokenInterceptor;
import com.sjxm.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private JwtTokenInterceptor jwtTokenInterceptor;

    protected void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(jwtTokenInterceptor)
                .addPathPatterns("/user/**")
                .excludePathPatterns("/user/account/login")
                .excludePathPatterns("/user/account/info")
                .excludePathPatterns("/user/barrage/list")
                .excludePathPatterns("/user/category/homecategory")
                .excludePathPatterns("/user/comment/page")
                .excludePathPatterns("/user/dynamic/page")
                .excludePathPatterns("/user/search/list")
                .excludePathPatterns("/user/video/page")
                .excludePathPatterns("/user/video/homesuggest")
                .excludePathPatterns("/user/video/info")
                .excludePathPatterns("/user/video/search")
                .excludePathPatterns("/user/common/uploadPic")
                .excludePathPatterns("/user/article/list")
        ;
    }

    /**
     * 使用knife4j生成接口文档
     * @return
     */
    @Bean
    public Docket docket(){
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("bilibiliPro文档接口")
                .version("2.0")
                .description("sjxm自制的bilibiliPro系统接口管理")
                .build();
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .groupName("用户端接口")
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.sjxm.controller"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }

    /**
     * 设置静态资源映射
     * @param registry
     */
    protected void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters){
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new JacksonObjectMapper());
        //将自定义消息转换器加入容器
        converters.add(0,converter);
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        long timeout = 4 * 1000;
        WebMvcConfiguration.super.configureAsyncSupport(configurer);
        configurer.setDefaultTimeout(timeout);
    }
}
