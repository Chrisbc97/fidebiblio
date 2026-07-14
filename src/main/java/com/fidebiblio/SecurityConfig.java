package com.fidebiblio;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    public static final String[] PUBLIC_URL = {"/login", "/fav/**", "/css/**", "/js/**", "/webjars/**"};
    public static final String[] ADMIN_URL = {"/usuario/**"};
    public static final String[] BIBLIOTECARIO_OR_ADMIN_URL = {"/libro/**", "/prestamo/listado", "/prestamo/renovar", "/prestamo/finalizar", "/sugerencia/estado"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request -> request
                .requestMatchers(PUBLIC_URL).permitAll()
                .requestMatchers(ADMIN_URL).hasRole("ADMINISTRADOR")
                .requestMatchers(BIBLIOTECARIO_OR_ADMIN_URL).hasAnyRole("ADMINISTRADOR", "BIBLIOTECARIO")
                .anyRequest().authenticated()
        ).formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
        ).logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        ).exceptionHandling(exceptions -> exceptions
                .accessDeniedPage("/acceso_denegado")
        ).sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
        );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService users(PasswordEncoder passwordEncoder) {
        UserDetails christopher = User.builder()
                .username("cbrenes00620@ufide.ac.cr")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMINISTRADOR")
                .build();
        UserDetails andrea = User.builder()
                .username("asolis00001@ufide.ac.cr")
                .password(passwordEncoder.encode("biblio123"))
                .roles("BIBLIOTECARIO")
                .build();
        UserDetails mateo = User.builder()
                .username("mvargas00002@ufide.ac.cr")
                .password(passwordEncoder.encode("estu123"))
                .roles("ESTUDIANTE")
                .build();
        return new InMemoryUserDetailsManager(christopher, andrea, mateo);
    }
}