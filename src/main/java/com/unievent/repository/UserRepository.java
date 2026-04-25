package com.unievent.repository;

import com.unievent.entity.User;
import com.unievent.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Admin queries
    List<User> findByRole(Role role);

    List<User> findByApprovalStatus(User.ApprovalStatus approvalStatus);

    List<User> findByRoleAndApprovalStatus(Role role, User.ApprovalStatus approvalStatus);

    List<User> findByNameContainingIgnoreCase(String name);

    List<User> findByNameContainingIgnoreCaseAndRole(String name, Role role);
}
