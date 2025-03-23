package com.pc.ecom.Security.Service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pc.ecom.Model.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;
    private Long id;

    private String username;

    @JsonIgnore  //Don't log sensitive info
    private String password;

    private String email;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Collection<? extends GrantedAuthority> authorities, String email, String password, String username) {
        this.authorities = authorities;
        this.email = email;
        this.password = password;
        this.username = username;
    }

    /**
     * Object mapper, converts a User object into UserDetails
     * @param user
     * @return
     */
    public static UserDetailsImpl build(User user) {

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                authorities,
                user.getEmail(),
                user.getPassword(),
                user.getUserName()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
