package com.linh.warehouse.mapper;

import com.linh.warehouse.dto.request.UserCreationRequest;
import com.linh.warehouse.dto.request.UserUpdateRequest;
import com.linh.warehouse.dto.response.UserResponse;
import com.linh.warehouse.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    UserResponse toUserResponse(User user);

    // Password sẽ xử lý riêng trong service, nên ignore tại đây
    @Mapping(target = "password", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
