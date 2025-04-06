package com.lowcost.model;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int id;
    private String email;
    private String fullName;
    private String password;
    private LocalDateTime registeredAt;
}