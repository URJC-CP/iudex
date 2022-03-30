package es.urjc.etsii.grafo.iudex;

import es.urjc.etsii.grafo.iudex.services.ResultHandler;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TheJudgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TheJudgeApplication.class, args);
        new ResultHandler();
    }

    @Bean
    public OpenAPI api() {
        return new OpenAPI().info(new Info().title("JudgeApi")
        .description("sample")
        .version("v0.0.1-SNAPSHOT"));
    }
}
