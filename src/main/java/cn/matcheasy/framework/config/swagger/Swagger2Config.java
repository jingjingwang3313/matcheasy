package cn.matcheasy.framework.config.swagger;

import cn.matcheasy.framework.constant.ProjectConst;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @class Swagger2Config
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: swagger文档:  http://127.0.0.1:80/docs.html
 * http://127.0.0.1:80/doc.html
 */
@Slf4j
@Configuration
@EnableSwagger2
@EnableKnife4j
public class Swagger2Config
{

    @Value("${swagger2.title}")
    private String title;

    @Value("${swagger2.apiPackage}")
    private String apiPackage;

    @Value("${swagger2.enable}")
    private boolean enable;

    @Bean
    public Docket ProductRestApi()
    {

        //添加header参数
        ParameterBuilder ticketPar = new ParameterBuilder();
        ticketPar.name(ProjectConst.ACCESS_TOKEN).description("用户token,根据接口确定是否必填")
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false).build();
        List<Parameter> pars = new ArrayList<>();
        pars.add(ticketPar.build());

        return new Docket(DocumentationType.SWAGGER_2)
                .pathMapping("/")
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(false)
                .globalOperationParameters(pars)
                .select()
                .apis(RequestHandlerSelectors.basePackage(apiPackage))//包扫描接口
                //.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))//包注解接口
                .paths(PathSelectors.any())
                .build().apiInfo(apiInfo()).enable(enable);
    }

    private ApiInfo apiInfo()
    {
        ApiInfo apiInfo = new ApiInfo(title,
                "description",
                "1.0.0",
                "API TERMS URL",
                "contactName",
                "license",
                "licenseUrl");
        return apiInfo;
    }

}
