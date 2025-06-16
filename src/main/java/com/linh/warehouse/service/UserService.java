package com.linh.warehouse.service;

import com.linh.warehouse.dto.request.UserCreationRequest;
import com.linh.warehouse.dto.request.UserUpdateRequest;
import com.linh.warehouse.dto.response.UserResponse;
import com.linh.warehouse.entity.User;
import com.linh.warehouse.enums.Role;
import com.linh.warehouse.exception.AppException;
import com.linh.warehouse.exception.ErrorCode;
import com.linh.warehouse.mapper.UserMapper;
import com.linh.warehouse.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor // thay @Autowired
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) // thay @Autowired
@Slf4j
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;

    public User createUser(UserCreationRequest request) {

    if(userRepository.existsByEmail(request.getEmail()))
        throw new AppException(ErrorCode.USER_EXISTED);

    User user = userMapper.toUser(request);
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    user.setPassword(passwordEncoder.encode(request.getPassword()));


    return userRepository.save(user);
    }

    public UserResponse updateUser(Integer id,UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        userMapper.updateUser(user, request);

        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('MANAGER')")
    public List<UserResponse> getUsers(){
        log.info("getUsers()");
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse).toList();
    }

    public UserResponse getUser(Integer id) {
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id)));
    }

    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
}
