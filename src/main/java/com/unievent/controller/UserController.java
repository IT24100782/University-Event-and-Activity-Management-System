package com.unievent.controller;

import com.unievent.entity.User;
import com.unievent.entity.Role;
import com.unievent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Allow frontend access
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            return ResponseEntity.ok(userService.updateUser(id, userDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println(">>> LOGIN ATTEMPT: " + loginRequest.getEmail());
            User user = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
            System.out.println(">>> LOGIN SUCCESS: " + user.getEmail() + " (Role: " + user.getRole() + ")");
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            System.err.println(">>> LOGIN FAILED for " + loginRequest.getEmail() + ": " + e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            System.err.println(">>> LOGIN CRITICAL ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    // ============ ADMIN ENDPOINTS ============

    /**
     * Get all pending users
     */
    @GetMapping("/admin/pending")
    public ResponseEntity<List<User>> getPendingUsers() {
        return ResponseEntity.ok(userService.getPendingUsers());
    }

    /**
     * Get pending users by role
     */
    @GetMapping("/admin/pending/role/{role}")
    public ResponseEntity<List<User>> getPendingUsersByRole(@PathVariable Role role) {
        return ResponseEntity.ok(userService.getPendingUsersByRole(role));
    }

    /**
     * Get users by role (e.g., STUDENT, CLUB_ADMIN, DEPT_ADMIN, SUPER_ADMIN)
     */
    @GetMapping("/admin/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable Role role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    /**
     * Search users by name
     */
    @GetMapping("/admin/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String name) {
        return ResponseEntity.ok(userService.searchUsersByName(name));
    }

    /**
     * Search users by name and role
     */
    @GetMapping("/admin/search/role/{role}")
    public ResponseEntity<List<User>> searchUsersByRole(@RequestParam String name, @PathVariable Role role) {
        return ResponseEntity.ok(userService.searchUsersByNameAndRole(name, role));
    }

    /**
     * Get users by approval status
     */
    @GetMapping("/admin/approval/{status}")
    public ResponseEntity<List<User>> getUsersByApprovalStatus(@PathVariable User.ApprovalStatus status) {
        return ResponseEntity.ok(userService.getUsersByApprovalStatus(status));
    }

    /**
     * Approve user registration
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<User> approveUser(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.approveUser(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Reject user registration
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<User> rejectUser(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.rejectUser(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
