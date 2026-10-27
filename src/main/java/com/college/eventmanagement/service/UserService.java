package com.college.eventmanagement.service;

import com.college.eventmanagement.dto.AuthRequestDto;
import com.college.eventmanagement.dto.AuthResponseDto;
import com.college.eventmanagement.dto.UserRegisterDto;
import com.college.eventmanagement.model.User;

import java.util.List;

public interface UserService {
    AuthResponseDto registerUser(UserRegisterDto registerDto);
    AuthResponseDto loginUser(AuthRequestDto loginDto);
    User getUserByEmail(String email);
    User getUserById(Long id);
    List<User> getAllUsers();
}
