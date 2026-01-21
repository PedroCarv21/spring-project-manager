package com.example.springprojectmanager.security;

import com.example.springprojectmanager.repositories.UsuarioRepository;
import com.example.springprojectmanager.services.UsuarioService;
import lombok.Builder;
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
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain criarSecurityFilterChain(HttpSecurity httpSecurity){
        return httpSecurity.
                csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .formLogin(config -> config.loginPage("/login").permitAll())
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
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
