package com.spring.getready.controller.api;

import com.spring.getready.dto.ApiResponse;
import com.spring.getready.dto.LoginRequest;
import com.spring.getready.dto.LoginResponse;
import com.spring.getready.dto.UserDTO;
import com.spring.getready.model.UserDetail;
import com.spring.getready.repository.UserDetailRepository;
import com.spring.getready.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailRepository userDetailRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateJwtToken(authentication);

            UserDetail userDetail = userDetailRepository.findByEmailEquals(loginRequest.getEmail());
            if (userDetail != null) {
                String role = userDetail.getUserGroup().getShortGroup();

                LoginResponse loginResponse = new LoginResponse(
                        jwt,
                        userDetail.getUserId(),
                        userDetail.getUsername(),
                        userDetail.getEmail(),
                        role
                );

                return ResponseEntity.ok(ApiResponse.success("Login successful", loginResponse));
            }

            return ResponseEntity.badRequest().body(ApiResponse.error("User not found"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid email or password"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
            }

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();

            UserDetail userDetail = userDetailRepository.findByEmailEquals(email);
            if (userDetail != null) {

                UserDTO userDTO = new UserDTO(
                        userDetail.getUserId(),
                        userDetail.getUsername(),
                        userDetail.getEmail(),
                        userDetail.getUserUuid(),
                        userDetail.getCreatedOn(),
                        userDetail.getLastLoginOn(),
                        userDetail.getIsLocked(),
                        userDetail.getUserGroup().getShortGroup(),
                        userDetail.getUserGroup().getGroupName()
                );

                return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", userDTO));
            }

            return ResponseEntity.badRequest().body(ApiResponse.error("User not found"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error retrieving user: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(ApiResponse.success("Logout successful"));
    }
}
