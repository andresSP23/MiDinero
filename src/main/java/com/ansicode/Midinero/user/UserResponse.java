package com.ansicode.Midinero.user;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private String firstName;
    private String lastName;
    private String email;
    private String fullName;
}
