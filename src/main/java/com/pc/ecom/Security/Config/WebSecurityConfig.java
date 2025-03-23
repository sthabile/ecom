package com.pc.ecom.Security.Config;

import com.pc.ecom.Config.AppConstants.AppRole;
import com.pc.ecom.Security.Jwt.AuthEntryPointJwt;
import com.pc.ecom.Security.Jwt.AuthTokenFilter;
import com.pc.ecom.Security.Service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.pc.ecom.Model.Role;
import com.pc.ecom.Model.User;
import com.pc.ecom.Repository.RoleRepository;
import com.pc.ecom.Repository.UserRepository;

import java.util.Set;

import static com.pc.ecom.Security.Utils.SecurityUtils.authorizeH2Console;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt authEntryPointJwt;

    @Bean
    public AuthTokenFilter authJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    /**
     * Need to specify config for auth provider as we are using a custom UserDetailsService
     * Would normally relly on spring security to pick up the user details service??
     * @return
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Nice thing here is that we could modify to provide a custom encoder
     * so long as it extends PasswordEncoder
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPointJwt))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests ->
                        requests.requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/api/public/").permitAll()
                                .requestMatchers("/images/**").permitAll()
                                .requestMatchers("api/admin/**").permitAll() //should not permit in PROD. Must always authenticate for this endpoint
                                .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        authorizeH2Console(http);

        return http.build();
    }

    /**
     * Use to expose specific endpoints where authentication isn't necessarily desired.
     * How does it differ from what the filter chain does?
     * Non-secure endpoints. E.G static endpoints
     * This is at a global level, otherwise can configure this on the filter chain instead
     * @return
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return webSecurity -> {
            webSecurity.ignoring().requestMatchers("/v2/api-docs/**",
                    "/configuration",
                    "/swagger-resources",
                    "/swagger-ui.html",
                    "/webjars/**");
        };
    }

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Retrieve or create roles
            Role userRole = roleRepository.findByRoleName(AppRole.USER)
                    .orElseGet(() -> {
                        Role newUserRole = new Role(AppRole.USER);
                        return roleRepository.save(newUserRole);
                    });

            Role sellerRole = roleRepository.findByRoleName(AppRole.SELLER)
                    .orElseGet(() -> {
                        Role newSellerRole = new Role(AppRole.SELLER);
                        return roleRepository.save(newSellerRole);
                    });

            Role adminRole = roleRepository.findByRoleName(AppRole.ADMIN)
                    .orElseGet(() -> {
                        Role newAdminRole = new Role(AppRole.ADMIN);
                        return roleRepository.save(newAdminRole);
                    });

            Set<Role> userRoles = Set.of(userRole);
            Set<Role> sellerRoles = Set.of(sellerRole);
            Set<Role> adminRoles = Set.of(userRole, sellerRole, adminRole);


            // Create users if not already present
            if (!userRepository.existsByUserName("user1")) {
                User user1 = new User("user1", passwordEncoder.encode("password1"),"user1@example.com");
                userRepository.save(user1);
            }

            if (!userRepository.existsByUserName("seller1")) {
                User seller1 = new User("seller1",passwordEncoder.encode("password2"),"seller1@example.com");
                userRepository.save(seller1);
            }

            if (!userRepository.existsByUserName("admin")) {
                User admin = new User("admin", passwordEncoder.encode("adminPass"), "admin@example.com");
                userRepository.save(admin);
            }

            // Update roles for existing users
            User user = userRepository.findByUserName("user1");
            if(user!=null){
                user.setRoles(userRoles);
                userRepository.save(user);
            }

            user = userRepository.findByUserName("seller1");
            if(user!=null){
                user.setRoles(sellerRoles);
                userRepository.save(user);
            }

            user = userRepository.findByUserName("admin");
            if(user!=null){
                user.setRoles(adminRoles);
                userRepository.save(user);
            }
        };
    }

}
