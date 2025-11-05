package com.study.springbootdeveloper.repository;

import com.study.springbootdeveloper.domain.User;
import com.study.springbootdeveloper.type.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    long countByRole(UserRole role);
}