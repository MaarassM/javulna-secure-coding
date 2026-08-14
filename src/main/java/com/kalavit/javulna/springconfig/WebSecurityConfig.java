package com.kalavit.javulna.springconfig;
import com.kalavit.javulna.security.JwtAuthFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    CustomAuthenticationSuccessHandler successHandler;

    @Autowired
    CustomAuthenticationFailureHandler failureHandler;

    @Autowired
    CustomLogoutSuccessHandler logouthandler;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(MyServletConfig.ALLOWED_CORS_ORIGINS));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList(MyServletConfig.ALLOWED_HEADERS));
        configuration.setAllowCredentials(Boolean.TRUE);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(MyServletConfig.CORS_MAPPNIG, configuration);
        return source;
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetailsService manager = new UserDetailsServiceImpl();
        return manager;
    }
    
    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .headers()
                    .frameOptions().deny()
                    .contentSecurityPolicy("default-src 'self'").and()
                    .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN).and()
                .and().csrf().disable()
                .formLogin()
                .usernameParameter("username")
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .and().logout().logoutSuccessHandler(logouthandler)
                .and().cors()
                .and().exceptionHandling().defaultAuthenticationEntryPointFor(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        new AntPathRequestMatcher("/rest/**")
                )
                .and().authorizeRequests()
                .and().authorizeRequests()
                .antMatchers("/error").permitAll()
                .antMatchers(HttpMethod.GET, "/rest/movie/**", "/movies", "/", "/webjars/**", "/js/**").permitAll()
                .antMatchers("/addMovie").permitAll()
                .antMatchers("/rest/auth/**").permitAll()
                .anyRequest().authenticated();
                http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


}
