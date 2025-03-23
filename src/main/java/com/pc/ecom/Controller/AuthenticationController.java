package com.pc.ecom.Controller;

import com.pc.ecom.Config.AppConstants;
import com.pc.ecom.Model.Role;
import com.pc.ecom.Model.User;
import com.pc.ecom.Repository.RoleRepository;
import com.pc.ecom.Repository.UserRepository;
import com.pc.ecom.Security.Jwt.JwtUtils;
import com.pc.ecom.Security.Payload.Request.LoginRequest;
import com.pc.ecom.Security.Payload.Request.SignupRequest;
import com.pc.ecom.Security.Payload.Response.MessageResponse;
import com.pc.ecom.Security.Payload.Response.UserInfoResponse;
import com.pc.ecom.Security.Service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping("/api/auth")
@RestController
public class AuthenticationController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public ResponseEntity<UserInfoResponse> signin(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        //save context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //generate the token
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        //Using token
//        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        //Using cookies
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        //fetch roles
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        //respond
//        UserInfoResponse response = new UserInfoResponse(userDetails.getUsername(), jwtToken, roles);

        UserInfoResponse response = new UserInfoResponse(userDetails.getId(),userDetails.getUsername(),roles);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(response);
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest){
        //if user existsByUserName()
        if(userRepository.existsByUserName(signupRequest.getUsername())){
            return ResponseEntity.badRequest().body(new MessageResponse("Username already exists"));
        }

        //if user existsByEmail()
        if(userRepository.existsByEmail(signupRequest.getEmail())){
            return ResponseEntity.badRequest().body(new MessageResponse("Email already exists"));
        }

        User user = new User(signupRequest.getUsername(),
                passwordEncoder.encode(signupRequest.getPassword()),
                signupRequest.getEmail());

        Set<String> rolesStr = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if(rolesStr == null){
            Role userRole = roleRepository.findByRoleName(AppConstants.AppRole.USER)
                    .orElseThrow(()-> new RuntimeException("Role is not found"));

            roles.add(userRole);
        }
        else {
            rolesStr.forEach(role -> {
                switch (role){
                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(AppConstants.AppRole.ADMIN)
                                .orElseThrow(()-> new RuntimeException("Role is not found"));

                        roles.add(adminRole);
                        break;
                    case "user":
                        Role sellerRole = roleRepository.findByRoleName(AppConstants.AppRole.SELLER)
                                .orElseThrow(()-> new RuntimeException("Role is not found"));

                        roles.add(sellerRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByRoleName(AppConstants.AppRole.USER)
                                .orElseThrow(()-> new RuntimeException("Role is not found"));

                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }

    /**
     * Retrieve the username of the currently authenticated user/ logged-in user
     * @param authentication
     * @return
     */
    @GetMapping("/username")
    public String currentUsername(Authentication authentication) {
        if(authentication!=null){
            return authentication.getName();
        }
        else
            return "NULL";
    }

    @GetMapping("/userdetails")
    public ResponseEntity<?> currentUserDetails(Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        //fetch roles
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(userDetails.getId(),userDetails.getUsername(),roles);

        return ResponseEntity.ok().body(response);
    }

    /**
     * Cookies will technically only be invalid after the expiration time/date
     * This allows us to invalidate a cookie before that.
     * @return
     */
    @PostMapping("signout")
    public ResponseEntity<?> signOutUser(){
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString())
                .body(new MessageResponse("You've signed out"));
    }
}
