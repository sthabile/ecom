package com.pc.ecom.Security.Config;

import com.pc.ecom.Security.Jwt.AuthEntryPointJwt;
import com.pc.ecom.Security.Jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.pc.ecom.Security.Utils.SecurityUtils.authorizeH2Console;

//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
public class SecurityConfig {
//
//    @Autowired
//    private AuthEntryPointJwt authenticationEntryPoint;
//
//    @Autowired
//    private AuthTokenFilter authTokenFilter;
//
//    @Bean
//    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
//        http.authorizeHttpRequests(
//                authorizeRequests ->
//                        authorizeRequests.requestMatchers("/h2-console/**").permitAll()
//                                .requestMatchers("/signin").permitAll()
//                                .anyRequest().authenticated());
//
//        http.sessionManagement(sessionManagement ->
//                sessionManagement.sessionCreationPolicy(
//                        SessionCreationPolicy.STATELESS
//                ));
//
//        authorizeH2Console(http);
//
//        /*
//            JWT config
//            unauthorized exception handler + add auth filter to the filter chain
//            Add it before all other filters to enforce security/authentication
//         */
//        http.exceptionHandling(exceptionHandling ->
//                exceptionHandling.authenticationEntryPoint(authenticationEntryPoint));
//
//        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }

}
