package es.urjc.etsii.grafo.iudex.spa;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**") // All requests to root path
                .addResourceLocations("/angular", "classpath:/static/") // try to serve them from /angular folder if it exists, this folder is mounted in docker-compose. If not, use the classpath.
                .setCacheControl(CacheControl.noCache()); // Disable cache for front assets for now
    }
}