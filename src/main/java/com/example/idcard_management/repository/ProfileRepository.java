package com.example.idcard_management.repository;

import com.example.idcard_management.model.Profile;
import com.example.idcard_management.model.ProfileType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUuid(String uuid);

    Optional<Profile> findByRegistrationNumber(String registrationNumber);

    boolean existsByRegistrationNumber(String registrationNumber);

    List<Profile> findByType(ProfileType type);
}
