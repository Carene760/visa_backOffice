package com.teamlead.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptor qui ajoute le header 'ngrok-skip-browser-warning' à toutes les réponses
 */
@Component
public class NgrokHeaderInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
        HttpServletRequest request, 
        HttpServletResponse response, 
        Object handler
    ) throws Exception {
        // Ajouter le header ngrok-skip-browser-warning pour bypass l'avertissement ngrok
        response.addHeader("ngrok-skip-browser-warning", "69");
        return true;
    }
}
