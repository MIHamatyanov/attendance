package ru.vmk.attendance.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String role;

    public JwtResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }
}
