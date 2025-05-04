package com.pc.ecom.Utils;

import com.pc.ecom.Model.User;
import com.pc.ecom.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {

    @Autowired
    UserRepository userRepository;

    public String loggedEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserName(authentication.getName());
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user.getEmail();
    }

    public User loggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserName(authentication.getName());
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    public Long loggedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserName(authentication.getName());
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user.getUserId();
    }
}
