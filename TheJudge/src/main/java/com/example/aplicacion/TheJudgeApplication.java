package com.example.aplicacion;

import com.example.aplicacion.services.ResultHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableSwagger2
@SpringBootApplication
public class TheJudgeApplication {

    public static void main(String[] args) {

        SpringApplication.run(TheJudgeApplication.class, args);
        //new DockerHelloWorld();
        new ResultHandler();
    }


    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("com.example.aplicacion.Controllers.apiControllers"))
                .paths(PathSelectors.any())
                .build();
    }

}
