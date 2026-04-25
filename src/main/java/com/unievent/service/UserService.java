package com.unievent.service;

import com.unievent.entity.User;
import com.unievent.entity.Role;
import com.unievent.repository.ClubMembershipRepository;
import com.unievent.repository.FeedbackRepository;
import com.unievent.repository.RegistrationRepository;
import com.unievent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ClubMembershipRepository clubMembershipRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }

        // Prevent creation of ADMIN users via this method (usually used by signup)
        if (user.getRole() == Role.ADMIN) {
            // For safety, only allow superadmin creation through seed / internal tools
            throw new RuntimeException("Admin accounts cannot be created via public sign-up");
        }

        // Validate university email for students and club admins
        if (user.getRole() == Role.STUDENT || user.getRole() == Role.CLUB_ADMIN) {
            String email = user.getEmail().toLowerCase();
            if (!email.endsWith("@my.sliit.lk")) {
                throw new RuntimeException("University email required (must end with @my.sliit.lk)");
            }
        }

        // Validate password complexity
        String password = user.getPassword();
        if (password == null || password.length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters long");
        }
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasSpecial = password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");

        if (!hasUpper || !hasLower || !hasSpecial) {
            throw new RuntimeException("Password must contain at least one uppercase letter, one lowercase letter, and one special character");
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setName(userDetails.getName());
        if (userDetails.getEmail() != null && !userDetails.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userDetails.getEmail())) {
                throw new RuntimeException("Email already taken");
            }
            user.setEmail(userDetails.getEmail());
        }
        // Only update password if provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        user.setUniversityId(userDetails.getUniversityId());
        user.setRole(userDetails.getRole());
        user.setActive(userDetails.isActive());

        return userRepository.save(user);
    }

    @jakarta.transaction.Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Hard delete: first remove all dependent records to avoid FK constraint violations
        registrationRepository.deleteAll(registrationRepository.findByStudentId(id));
        feedbackRepository.deleteAll(feedbackRepository.findByStudentId(id));
        clubMembershipRepository.deleteByStudentId(id);

        // Now permanently delete the user
        userRepository.delete(user);

        System.out.println("User permanently deleted: " + user.getEmail());
    }

    public User loginUser(String email, String password) {
        String trimmedEmail = email.trim().toLowerCase();
        String trimmedPassword = password.trim();

        User user = userRepository.findByEmail(trimmedEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + trimmedEmail));

        if (!passwordEncoder.matches(trimmedPassword, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        if (!user.isActive()) {
            throw new RuntimeException("User account is inactive");
        }
        user.setLastLogin(java.time.LocalDateTime.now());
        return userRepository.save(user);
    }

    // Admin methods for user management

    /**
     * Get all users by role
     */
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    /**
     * Get all users with pending approval
     */
    public List<User> getPendingUsers() {
        return userRepository.findByApprovalStatus(User.ApprovalStatus.PENDING);
    }

    /**
     * Get pending users by specific role
     */
    public List<User> getPendingUsersByRole(Role role) {
        return userRepository.findByRoleAndApprovalStatus(role, User.ApprovalStatus.PENDING);
    }

    /**
     * Search users by name
     */
    public List<User> searchUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Search users by name and role
     */
    public List<User> searchUsersByNameAndRole(String name, Role role) {
        return userRepository.findByNameContainingIgnoreCaseAndRole(name, role);
    }

    /**
     * Approve user registration
     */
    public User approveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setApprovalStatus(User.ApprovalStatus.APPROVED);
        user.setActive(true);
        return userRepository.save(user);
    }

    /**
     * Reject user registration
     */
    public User rejectUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setApprovalStatus(User.ApprovalStatus.REJECTED);
        user.setActive(false);
        return userRepository.save(user);
    }

    /**
     * Get users by approval status
     */
    public List<User> getUsersByApprovalStatus(User.ApprovalStatus status) {
        return userRepository.findByApprovalStatus(status);
    }
}
