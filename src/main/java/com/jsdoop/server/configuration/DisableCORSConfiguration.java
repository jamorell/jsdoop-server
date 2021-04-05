package com.jsdoop.server.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class DisableCORSConfiguration implements WebMvcConfigurer  {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
       // .allowedOrigins(
       //       "http://localhost:8085")
        
        
//        .allowedOrigins(
//                "http://localhost:3000",..)

//        .allowedHeaders("Content-Type")
//        .allowedHeaders("x-xsrf-token")
//        .allowedHeaders("Authorization")
//        .allowedHeaders("Access-Control-Allow-Headers")
//        .allowedHeaders("Origin")
//        .allowedHeaders("Accept")
//        .allowedHeaders("X-Requested-With")
//        .allowedHeaders("Access-Control-Request-Method")
//        .allowedHeaders("Access-Control-Request-Headers")
//        .allowedHeaders("Access-Control-Request-Headers")
        	
            .allowedHeaders("current_age", "id_job", "Location")
            .exposedHeaders("current_age", "id_job", "Location")
        .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
//        .allowCredentials(true);    	
    	

    }
}
