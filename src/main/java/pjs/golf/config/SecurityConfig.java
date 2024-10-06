package pjs.golf.config;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import pjs.golf.config.filter.TokenAccessDeniedHandler;
import pjs.golf.config.filter.TokenAuthenticationEntryPoint;
import pjs.golf.config.token.TokenManager;
import pjs.golf.config.token.TokenSecurityConfig;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final TokenManager tokenManager;
    private final TokenAuthenticationEntryPoint tokenAuthenticationEntryPoint;
    private final TokenAccessDeniedHandler tokenAccessDeniedHandler;

    @Value("${path.real}")
    private String REAL_PATH;

    @Value("${path.dev.http}")
    private String DEV_HTTP;

    @Value("${path.dev.https}")
    private String DEV_HTTPS;


    public SecurityConfig(TokenManager tokenManager, TokenAuthenticationEntryPoint tokenAuthenticationEntryPoint, TokenAccessDeniedHandler tokenAccessDeniedHandler) {
        this.tokenManager = tokenManager;
        this.tokenAuthenticationEntryPoint = tokenAuthenticationEntryPoint;
        this.tokenAccessDeniedHandler = tokenAccessDeniedHandler;
    }

    CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setAllowedMethods(Collections.singletonList("*"));
            config.setAllowedOriginPatterns(List.of(REAL_PATH, DEV_HTTP, DEV_HTTPS, "https://localhost", "http://localhost"));
            config.setAllowCredentials(true);
            config.setExposedHeaders(List.of("Sse-Connection-Id"));
            return config;
        };
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        String[] swaggerPatterns = {"/v3/api-docs/**", "/configuration/ui",
                "/swagger-resources/**", "/configuration/security",
                "/swagger-ui/**", "/swagger/**", "/swagger-ui.html"};

        httpSecurity
                .httpBasic(HttpBasicConfigurer::disable)
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
                .csrf(CsrfConfigurer::disable)
                .sessionManagement(configuer ->
                        configuer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize ->
                        authorize
                                .anyRequest().permitAll()
                )
                .exceptionHandling(authenticationManager ->
                        authenticationManager
                                .authenticationEntryPoint(tokenAuthenticationEntryPoint)
                                .accessDeniedHandler(tokenAccessDeniedHandler)
                )
                .with(new TokenSecurityConfig(tokenManager), Customizer.withDefaults());

        return httpSecurity.build();
    }

}