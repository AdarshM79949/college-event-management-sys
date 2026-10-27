package com.college.eventmanagement.dto;

import com.college.eventmanagement.model.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDto {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private String collegeId;
    private String message;

    public AuthResponseDto() {}

    public AuthResponseDto(Long id, String name, String email, Role role, String collegeId, String message) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.collegeId = collegeId;
        this.message = message;
    }
}
