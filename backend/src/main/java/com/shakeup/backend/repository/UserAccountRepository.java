package com.shakeup.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shakeup.backend.domain.Role;
import com.shakeup.backend.domain.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

	Optional<UserAccount> findByEmailIgnoreCase(String email);

	boolean existsByRole(Role role);

	boolean existsByEmailIgnoreCase(String email);

	List<UserAccount> findByRoleOrderByDisplayNameAsc(Role role);

	Optional<UserAccount> findByIdAndRole(Long id, Role role);
}
