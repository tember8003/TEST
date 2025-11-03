package com.study.springbootdeveloper.repository;

import com.study.springbootdeveloper.domain.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshRepository extends JpaRepository<RefreshEntity, Long> {
    void deleteByRefresh(String refresh);

    boolean existsByRefresh(String refresh);
}
