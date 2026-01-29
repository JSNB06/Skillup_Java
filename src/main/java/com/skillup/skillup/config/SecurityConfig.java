package com.skillup.skillup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new CustomSuccessHandler();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());

        // SIN ENCRIPTACIÓN
        authProvider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());

        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )


                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/login/**", "/images/**", "/css/**", "/home", "/registro", "/js/**", "/registro/**", "/api/reportes/cursos/pdf**").permitAll()
                        .requestMatchers("/administrador/**").hasRole("1")
                        .requestMatchers("/estudiante/**").hasRole("2")
                        .requestMatchers("/evaluador/**").hasRole("3")
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login/acceder")
                        .usernameParameter("usuario")
                        .passwordParameter("password")
                        .successHandler(successHandler())
                        .failureUrl("/login?error")
                )

                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/login/salir", "GET"))
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                )

                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives(
                                        "default-src 'self'; " +
                                                "script-src 'self' 'unsafe-inline' https://cdn.tailwindcss.com; " +
                                                "style-src 'self' 'unsafe-inline' https://cdn.tailwindcss.com; " +
                                                "font-src 'self' data:; " +
                                                "img-src 'self' data: https:;"
                                )
                        )
                )

                .authenticationProvider(authenticationProvider());

        return http.build();
    }
}