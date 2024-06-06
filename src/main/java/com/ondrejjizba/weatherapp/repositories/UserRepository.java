package com.ondrejjizba.weatherapp.repositories;

import com.ondrejjizba.weatherapp.models.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserInfo, Long> {
    boolean existsByUsername (String username);
}
