package com.linh.warehouse.service;

import com.linh.warehouse.dto.request.UserCreationRequest;
import com.linh.warehouse.dto.request.UserUpdateRequest;
import com.linh.warehouse.entity.User;
import com.linh.warehouse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User createUser(UserCreationRequest request) {
        User user = new User();

        // Chuyển đổi và đảm bảo giá trị nhập là hợp lệ
        User.Role role = User.Role.valueOf(request.getRole().toUpperCase());

        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFullname(request.getFullname());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setRole(role);
    return userRepository.save(user);
    }

    public User updateUser(Integer id,UserUpdateRequest request) {
        User user = getUser(id);

        // Chuyển đổi và đảm bảo giá trị nhập là hợp lệ
        User.Role role = User.Role.valueOf(request.getRole().toUpperCase());

        user.setPassword(request.getPassword());
        user.setFullname(request.getFullname());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setRole(role);

        return userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUser(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
}
