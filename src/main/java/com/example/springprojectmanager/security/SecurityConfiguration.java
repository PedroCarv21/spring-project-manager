package com.example.springprojectmanager.security;

import com.example.springprojectmanager.repositories.UsuarioRepository;
import com.example.springprojectmanager.services.UsuarioService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain criarSecurityFilterChain(HttpSecurity httpSecurity, LoginSuccessHandler loginSuccessHandler){
        return httpSecurity.
                csrf(AbstractHttpConfigurer::disable)
//                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/login", "/login/**", "/cadastro", "/cadastro/**", "/css/**", "/images/**").permitAll();
                    auth.anyRequest().authenticated();
                })
                .formLogin(config -> {
                    config.loginPage("/login")
                          .successHandler(loginSuccessHandler)
                          .permitAll();
                })
                .build();
    }

    @Bean
    public PasswordEncoder criarEncoder(){
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public UserDetailsService criarUserDetailsService(UsuarioRepository usuarioRepository){
        return new CustomUserDetailsService(usuarioRepository);
    }
}
