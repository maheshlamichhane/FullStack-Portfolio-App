package com.portfolio.app.config;

import com.portfolio.app.exception.CustomAccessDeniedHandler;
import com.portfolio.app.exception.CustomBasicAuthenticationEntryPoint;
import com.portfolio.app.filter.AuthoritiesLoggingAfterFilter;
import com.portfolio.app.filter.RequestValidationBeforeFilter;
import com.portfolio.app.handler.CustomAuthenticationFailureHandler;
import com.portfolio.app.handler.CustomAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception{
        http
                .sessionManagement(smc -> smc.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/company","/error","/register","/invalidSession","/assets/**","/login/**","/logout","/home").permitAll()
                        .requestMatchers("/admin/update","/admin/delete").hasRole("ADMIN")
                        .requestMatchers("/user/select").access(AuthorizationManagers.allOf(AuthorityAuthorizationManager.hasRole("USER"), AuthorityAuthorizationManager.hasAuthority("SELECTUSER")))
                        .requestMatchers("/user/selectinfo").access(AuthorizationManagers.allOf(AuthorityAuthorizationManager.hasRole("USER"), AuthorityAuthorizationManager.hasAuthority("SELECTINFO")))
                        .anyRequest().authenticated()
                );

        http.httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()));
        http.exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomAccessDeniedHandler()));
        http.addFilterBefore(new RequestValidationBeforeFilter(), BasicAuthenticationFilter.class);
        http.addFilterAfter(new AuthoritiesLoggingAfterFilter(),BasicAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}