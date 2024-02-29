package es.urjc.etsii.grafo.iudex.docs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

@Configuration
public class SwaggerDocs {
    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            var preAuthorizeAnnotation = Optional.ofNullable(handlerMethod.getMethodAnnotation(PreAuthorize.class));
            StringBuilder sb = new StringBuilder();
            if (preAuthorizeAnnotation.isPresent()) {
                sb.append("This api requires **")
                        .append((preAuthorizeAnnotation.get()).value().replaceAll("hasAuthority|\\(|\\)|'", ""))
                        .append("** permission.");
            } else {
                sb.append("This api is **public**");
            }
            sb.append("<br /><br />");
            if(operation.getDescription() == null){
                sb.append(operation.getSummary());
            } else {
                sb.append(operation.getDescription());
            }
            operation.setDescription(sb.toString());
            return operation;
        };
    }


    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
//                .flows(new OAuthFlows().authorizationCode(new OAuthFlow().
//                        scopes(new Scopes()
//                                .addString("ROLE_USER", "Default role granted to everyone")
//                                .addString("ROLE_JUDGE", "Role granted to contest judges")
//                                .addString("ROLE_ADMIN", "Role grantes to server administrators")
//                        )))
//                .scheme("bearer");
    }
    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info().title("JudgeApi").description("Next generation competitive programming judge"))
                .addSecurityItem(new SecurityRequirement().addList("Bearer"))
                .components(new Components().addSecuritySchemes("Bearer", createAPIKeyScheme()));
    }
}
