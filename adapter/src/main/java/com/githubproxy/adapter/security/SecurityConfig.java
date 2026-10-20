package com.githubproxy.adapter.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF — wyłączamy dla stateless REST API
                //    CSRF chroni przed atakami przez cookies.
                //    REST API z Bearer tokenami nie używa cookies → zbędny.
                .csrf(csrf -> csrf.disable())

                // 2. SESJE — stateless (brak sesji na serwerze)
                //    JWT jest self-contained → serwer nie pamięta stanu.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 5. Reguły dostępu — od NAJBARDZIEJ do NAJMNIEJ specyficznej
                //    Kolejność ma znaczenie! Pierwsza pasująca wygrywa.
                //    ZMIANA względem PDF: ścieżki z GitHubClientController zamiast przykładowych /api/...
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/repositories/**", "/local/repositories/**")
                        .hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/repositories/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/repositories/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/repositories/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakRoleConverter())))
                // 6. Custom error handling (KROK 9)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler()));

        return http.build();
    }

    private JwtAuthenticationConverter keycloakRoleConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess == null || !(realmAccess.get("roles") instanceof Collection<?> roles)) {
                return List.of();
            }
            return roles.stream().map(Object::toString)
                    .<GrantedAuthority>map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .toList();
        });
        converter.setPrincipalClaimName("preferred_username");
        return converter;
    }
}
