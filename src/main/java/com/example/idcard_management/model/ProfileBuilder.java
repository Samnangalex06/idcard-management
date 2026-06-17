package com.example.idcard_management.model;

public class ProfileBuilder {

    public static Profile createStudent() {

        return Profile.builder()
                .type(ProfileType.STUDENT)
                .department("IT")
                .build();
    }

    public static Profile createEmployee() {

        return Profile.builder()
                .type(ProfileType.EMPLOYEE)
                .department("HR")
                .build();
    }
}