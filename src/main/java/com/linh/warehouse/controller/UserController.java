package com.linh.warehouse.controller;

import com.linh.warehouse.dto.request.UserCreationRequest;
import com.linh.warehouse.dto.request.UserUpdateRequest;
import com.linh.warehouse.entity.User;
import com.linh.warehouse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    User createUser(@RequestBody UserCreationRequest request) {
        return userService.createUser(request);
    }

    @GetMapping
    List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    User getUserById(@PathVariable Integer id) {
        return userService.getUser(id);
    }

    @PutMapping("/{id}")
    User updateUser(@RequestBody UserUpdateRequest request, @PathVariable Integer id) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    String deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return "User deleted";
    }
}
