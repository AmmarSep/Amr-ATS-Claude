package com.spring.getready.controller.api;

import com.spring.getready.dto.ApiResponse;
import com.spring.getready.dto.UserDTO;
import com.spring.getready.model.UserDetail;
import com.spring.getready.model.UserGroup;
import com.spring.getready.repository.UserDetailRepository;
import com.spring.getready.repository.UserGroupRepository;
import com.spring.getready.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminRestController {

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${user.default-password}")
    private String defaultPassword;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(value = "role", required = false) String role) {
        try {
            List<UserDetail> users;

            if (role != null && !role.isEmpty()) {
                users = userDetailRepository.findByUserGroupShortGroupEquals(role);
            } else {
                users = userDetailRepository.findAll();
            }

            List<UserDTO> userDTOs = users.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", userDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error retrieving users: " + e.getMessage()));
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        try {
            Optional<UserDetail> userOpt = userDetailRepository.findById(id);
            if (!userOpt.isPresent()) {
                return ResponseEntity.status(404).body(ApiResponse.error("User not found"));
            }

            UserDTO userDTO = convertToDTO(userOpt.get());
            return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", userDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error retrieving user: " + e.getMessage()));
        }
    }

    @PostMapping("/recruiters")
    public ResponseEntity<?> createRecruiter(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String email = request.get("email");

            if (username == null || username.isEmpty() || email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Username and email are required"));
            }

            // Check if user already exists
            UserDetail existingUser = userDetailRepository.findByEmailEquals(email);
            if (existingUser != null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("User with this email already exists"));
            }

            // Get recruiter group
            UserGroup recruiterGroup = userGroupRepository.findByShortGroupEquals("REC");
            if (recruiterGroup == null) {
                return ResponseEntity.status(500).body(ApiResponse.error("Recruiter group not found"));
            }

            // Create new recruiter
            UserDetail recruiter = new UserDetail();
            recruiter.setUsername(username);
            recruiter.setEmail(email);
            recruiter.setPassword(passwordEncoder.encode(defaultPassword));
            recruiter.setUserUuid(UUID.randomUUID().toString());
            recruiter.setCreatedOn(new Timestamp(System.currentTimeMillis()));
            recruiter.setIsLocked(false);
            recruiter.setUserGroup(recruiterGroup);

            UserDetail savedRecruiter = userDetailRepository.save(recruiter);
            UserDTO userDTO = convertToDTO(savedRecruiter);

            return ResponseEntity.ok(ApiResponse.success("Recruiter created successfully", userDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error creating recruiter: " + e.getMessage()));
        }
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String email = request.get("email");
            String role = request.get("role"); // ADM, REC, CAN, USR

            if (username == null || username.isEmpty() || email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Username and email are required"));
            }

            // Check if user already exists
            UserDetail existingUser = userDetailRepository.findByEmailEquals(email);
            if (existingUser != null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("User with this email already exists"));
            }

            // Get user group
            String shortGroup = (role != null && !role.isEmpty()) ? role : "USR";
            UserGroup userGroup = userGroupRepository.findByShortGroupEquals(shortGroup);
            if (userGroup == null) {
                return ResponseEntity.status(500).body(ApiResponse.error("User group not found"));
            }

            // Create new user
            UserDetail user = new UserDetail();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(defaultPassword));
            user.setUserUuid(UUID.randomUUID().toString());
            user.setCreatedOn(new Timestamp(System.currentTimeMillis()));
            user.setIsLocked(false);
            user.setUserGroup(userGroup);

            UserDetail savedUser = userDetailRepository.save(user);
            UserDTO userDTO = convertToDTO(savedUser);

            return ResponseEntity.ok(ApiResponse.success("User created successfully", userDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error creating user: " + e.getMessage()));
        }
    }

    @PatchMapping("/users/{id}/toggle")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Integer id) {
        try {
            Optional<UserDetail> userOpt = userDetailRepository.findById(id);
            if (!userOpt.isPresent()) {
                return ResponseEntity.status(404).body(ApiResponse.error("User not found"));
            }

            UserDetail user = userOpt.get();
            user.setIsLocked(!user.getIsLocked());
            UserDetail savedUser = userDetailRepository.save(user);

            UserDTO userDTO = convertToDTO(savedUser);
            return ResponseEntity.ok(ApiResponse.success("User status updated successfully", userDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error toggling user status: " + e.getMessage()));
        }
    }

    @PostMapping("/users/{id}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable Integer id) {
        try {
            Optional<UserDetail> userOpt = userDetailRepository.findById(id);
            if (!userOpt.isPresent()) {
                return ResponseEntity.status(404).body(ApiResponse.error("User not found"));
            }

            UserDetail user = userOpt.get();
            user.setPassword(passwordEncoder.encode(defaultPassword));
            userDetailRepository.save(user);

            return ResponseEntity.ok(ApiResponse.success("Password reset successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error resetting password: " + e.getMessage()));
        }
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();

            // Count users by role
            long totalUsers = userDetailRepository.count();
            long recruiters = userDetailRepository.findByUserGroupShortGroupEquals("REC").size();
            long candidates = userDetailRepository.findByUserGroupShortGroupEquals("CAN").size();

            stats.put("totalUsers", totalUsers);
            stats.put("recruiters", recruiters);
            stats.put("candidates", candidates);

            return ResponseEntity.ok(ApiResponse.success("Dashboard stats retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error retrieving stats: " + e.getMessage()));
        }
    }

    private UserDTO convertToDTO(UserDetail user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setUserUuid(user.getUserUuid());
        dto.setCreatedOn(user.getCreatedOn());
        dto.setLastLoginOn(user.getLastLoginOn());
        dto.setIsLocked(user.getIsLocked());

        if (user.getUserGroup() != null) {
            dto.setRole(user.getUserGroup().getShortGroup());
            dto.setGroupName(user.getUserGroup().getGroupName());
        }

        return dto;
    }
}
