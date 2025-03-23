package com.pc.ecom.Repository;

import com.pc.ecom.Config.AppConstants;
import com.pc.ecom.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(AppConstants.AppRole appRole);
}
