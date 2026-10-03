package com.library.management.service;

import com.library.management.dto.ApiResponse;
import com.library.management.dto.PageResponse;
import com.library.management.dto.UserCreateRequest;
import com.library.management.dto.UserResponse;
import com.library.management.dto.UserUpdateRequest;
import com.library.management.entity.User;
import com.library.management.entity.UserRole;
import com.library.management.entity.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;

public interface UserService {

    UserResponse createUser(UserCreateRequest request);

    UserResponse getUserById(Long id);

    UserResponse getUserByUsername(String username);

    UserResponse getUserByEmail(String email);

    PageResponse<UserResponse> getAllUsers(Pageable pageable);

    PageResponse<UserResponse> searchUsers(String search, Pageable pageable);

    List<UserResponse> getUsersByRole(UserRole role);

    List<UserResponse> getUsersByStatus(UserStatus status);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    UserResponse updateUserStatus(Long id, UserStatus status);

    UserResponse updateUserRole(Long id, UserRole role);

    void deleteUser(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<UserResponse> getExpiredUsers();

    UserDetails loadUserByUsername(String username);
}