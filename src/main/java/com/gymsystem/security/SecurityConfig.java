package com.gymsystem.security;

import com.gymsystem.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtAuthorizationFilter jwtAuthorizationFilter) {
        this.userDetailsService = customUserDetailsService;
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/error").permitAll()
                    .antMatchers(HttpMethod.GET, "/api/users/login").permitAll()
                    .antMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                    .antMatchers(HttpMethod.GET,"/api/users/home").permitAll()
                    .antMatchers(HttpMethod.PUT, "/api/users/changePassword").permitAll()
                    .antMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
                    .antMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
                    .antMatchers(HttpMethod.POST, "/api/users/minutes").hasRole("ADMIN")
                    .antMatchers(HttpMethod.GET, "/api/users/{name}").permitAll() // for test html remove latter
    //                .antMatchers(HttpMethod.GET, "/api/users/{name}").hasAnyRole("ADMIN", "USER")
                    .antMatchers(HttpMethod.GET, "/api/users/{name}/history").hasAnyRole("ADMIN", "USER")
                    .anyRequest().authenticated()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
