package org.example.PetProjectShop.projectFiles.config;

import org.example.PetProjectShop.projectFiles.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecutiryConfig{
    private PersonService personService;

    @Autowired
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    // Для настройки Spring Security
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf -> {
                    try {
                        csrf.disable()
                        .authorizeHttpRequests(request -> request
                                .requestMatchers("/auth/login", "/auth/register", "/error").permitAll()
                                .anyRequest().authenticated());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        http
                .formLogin(form -> form
                        .loginPage("/auth/login").loginProcessingUrl("/process_login")
                        .permitAll()
                        .defaultSuccessUrl("/shop", true)
                        .failureUrl("/auth/login?error"));

        return http.build();
    }

    // Для настройки аутентификации
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(personService);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }
}
