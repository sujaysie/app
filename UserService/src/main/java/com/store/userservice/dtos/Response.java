package com.store.userservice.dtos;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class Response<T> {
    String message;
    @Nullable
    T data;
}
