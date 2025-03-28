package com.lowcost.model;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String id; // ID з Keycloak/Auth0
    private String email;
    private String fullName;
    private LocalDateTime registeredAt;
}