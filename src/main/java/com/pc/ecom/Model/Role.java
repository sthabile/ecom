package com.pc.ecom.Model;

import com.pc.ecom.Config.AppConstants.AppRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20,name = "role_name")
    private AppRole roleName;

    @ManyToMany
    private List<User> users;

    public Role(AppRole roleName) {
        this.roleName = roleName;
    }
}
