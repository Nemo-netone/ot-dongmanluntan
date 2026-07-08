package com.admin.sys.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    private static final long MAX_AGE = 24 * 60 * 60;
    private static final String DEFAULT_ORIGINS = "http://localhost:8088,http://localhost:8080,https://ot-dongmanluntan.pages.dev";

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        String origins = System.getenv().getOrDefault("CORS_ALLOWED_ORIGINS", DEFAULT_ORIGINS);
        for (String origin : origins.split(",")) {
            String trimmed = origin.trim();
            if (!trimmed.isEmpty()) {
                corsConfiguration.addAllowedOriginPattern(trimmed);
            }
        }
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addExposedHeader("token");
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setMaxAge(MAX_AGE);
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }
}
