package com.linh.warehouse.service;

import com.linh.warehouse.dto.request.UserCreationRequest;
import com.linh.warehouse.dto.request.UserUpdateRequest;
import com.linh.warehouse.dto.response.UserResponse;
import com.linh.warehouse.entity.User;
import com.linh.warehouse.exception.AppException;
import com.linh.warehouse.exception.ErrorCode;
import com.linh.warehouse.mapper.UserMapper;
import com.linh.warehouse.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // thay @Autowired
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) // thay @Autowired
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;

    public User createUser(UserCreationRequest request) {

    if(userRepository.existsByEmail(request.getEmail()))
        throw new AppException(ErrorCode.USER_EXISTED);

    User user = userMapper.toUser(request);

    return userRepository.save(user);
    }

    public UserResponse updateUser(Integer id,UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public UserResponse getUser(Integer id) {
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id)));
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
}
