package com.store.userservice.dtos;

import lombok.Data;

@Data
public class ChangeRoleRequest {
    Long userId;
    String role;
}
