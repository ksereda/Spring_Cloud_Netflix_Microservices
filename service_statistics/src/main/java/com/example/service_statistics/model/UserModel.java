package com.example.service_statistics.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserModel {

    private Long id;
    private String username;
    private String personalUserNumber;
    private String title;
    private Long postNumber;

}
