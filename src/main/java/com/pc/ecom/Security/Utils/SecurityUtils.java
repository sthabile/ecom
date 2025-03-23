package com.pc.ecom.Security.Utils;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;

public class SecurityUtils {
    /**
     * Additional config to allow access to the H2 console.
     * @param http
     * @throws Exception
     */
    public static void authorizeH2Console(HttpSecurity http) throws Exception {
        http.headers(headers -> {
            headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable);
        });

        http.csrf(AbstractHttpConfigurer::disable);
    }
}
