package com.letsplay.configue;

import com.letsplay.security.JwtAccessDeniedHandler;
import com.letsplay.security.JwtAuthenticationEntryPoint;
import com.letsplay.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtFilter;
        private final JwtAuthenticationEntryPoint authenticationEntryPoint;
        private final JwtAccessDeniedHandler accessDeniedHandler;

        public SecurityConfig(JwtAuthenticationFilter jwtFilter, JwtAuthenticationEntryPoint authenticationEntryPoint, JwtAccessDeniedHandler accessDeniedHandler) {

                this.jwtFilter = jwtFilter;
                this.authenticationEntryPoint = authenticationEntryPoint;
                this.accessDeniedHandler = accessDeniedHandler;
        }

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http

                                .csrf(csrf -> csrf.disable())

                                .cors(Customizer.withDefaults())

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint(authenticationEntryPoint)
                                                .accessDeniedHandler(accessDeniedHandler))

                                .authorizeHttpRequests(auth -> auth

                                                .requestMatchers("/auth/**").permitAll()

                                                .requestMatchers(HttpMethod.GET, "/products/**").permitAll()

                                                .requestMatchers(HttpMethod.POST, "/products/**")
                                                .hasAnyRole("USER", "ADMIN")

                                                .requestMatchers(HttpMethod.PUT, "/products/**")
                                                .hasAnyRole("USER", "ADMIN")

                                                .requestMatchers(HttpMethod.DELETE, "/products/**")
                                                .hasAnyRole("USER", "ADMIN")

                                                .requestMatchers("/users/**")
                                                .hasRole("ADMIN")

                                                .anyRequest().authenticated())

                                .addFilterBefore( jwtFilter, UsernamePasswordAuthenticationFilter.class);
                return http.build();
        }

}