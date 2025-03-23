package com.pc.ecom.Security.Service;

import com.pc.ecom.Model.User;
import com.pc.ecom.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    /**
     * Fetching the user details from the database
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    //Ensures the fetch completes fully otherwise rollback if encountered an error
    //Ensures data consistency and integrity
    @Transactional()
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username "+ username);
        }
        return UserDetailsImpl.build(user);
    }
}
