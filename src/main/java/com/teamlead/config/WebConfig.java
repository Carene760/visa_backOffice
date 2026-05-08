package com.teamlead.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration Web centralisée pour CORS et Intercepteurs
 * - CORS : Autorise toutes les origines pour les endpoints publics
 * - Intercepteur NGROK : Ajoute le header pour bypasser l'avertissement ngrok
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private NgrokHeaderInterceptor ngrokHeaderInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // CORS pour tous les endpoints - très permissif pour le développement
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Ajouter l'intercepteur qui ajoute le header ngrok-skip-browser-warning
        registry.addInterceptor(ngrokHeaderInterceptor);
    }
}
