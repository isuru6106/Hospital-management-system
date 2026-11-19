
package com.example.hospital_manager.dto;

import com.example.hospital_manager.model.Role;

public record UserResponse(

        Long id,

        String name,

        String username,

        Role role

) {
}
