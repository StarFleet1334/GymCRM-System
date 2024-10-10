package com.demo.folder.config;

import com.demo.folder.service.CustomUserDetailsService;
import com.demo.folder.service.LoginAttemptService;
import com.demo.folder.utils.BlockUserFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

  private final CustomUserDetailsService userDetailsService;

  @Autowired
  private LoginAttemptService loginAttemptService;


  @Autowired
  public SecurityConfig(@Lazy CustomUserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                new AntPathRequestMatcher("/api/trainees"),
                new AntPathRequestMatcher("/api/trainers"),
                new AntPathRequestMatcher("/api/training-type"),
                new AntPathRequestMatcher("/api/login"),
                new AntPathRequestMatcher("/swagger-ui.html"),
                new AntPathRequestMatcher("/swagger-ui/**"),
                new AntPathRequestMatcher("/v3/api-docs/**"),
                new AntPathRequestMatcher("/swagger-resources/**"),
                new AntPathRequestMatcher("/webjars/**"),
                new AntPathRequestMatcher("/actuator/health"),
                new AntPathRequestMatcher("/actuator/info"),
                new AntPathRequestMatcher("/actuator/prometheus"),
                new AntPathRequestMatcher("/h2-console")
            ).permitAll()
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginProcessingUrl("/api/login")
            .successHandler((request, response, authentication) -> {
              response.setStatus(HttpStatus.OK.value());
              response.getWriter().write("Login successful");
            })
            .failureHandler((request, response, exception) -> {
              response.setStatus(HttpStatus.UNAUTHORIZED.value());
              String username = request.getParameter("username");
              loginAttemptService.loginFailed(username);
              response.getWriter().write("Invalid credentials");
            })
        )
        .httpBasic(AbstractHttpConfigurer::disable)
        .exceptionHandling(exceptionHandling ->
            exceptionHandling.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        )
        .addFilterBefore(new BlockUserFilter(loginAttemptService), UsernamePasswordAuthenticationFilter.class)
        .userDetailsService(userDetailsService);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

}
