package com.store.userservice.dtos;

import com.store.userservice.models.Role;
import lombok.Data;

import java.util.List;

@Data
public class UserDto extends Response<UserDto>{
    String username;
    List<Role> roles;
}
