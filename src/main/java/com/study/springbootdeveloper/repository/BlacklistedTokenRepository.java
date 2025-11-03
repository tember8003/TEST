package com.study.springbootdeveloper.repository;

import com.study.springbootdeveloper.domain.BlacklistedTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedTokenEntity,Long> {

    boolean existsByToken(String token);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM BlacklistedTokenEntity b WHERE b.expiration < :currentTime")
    int deleteExpiredTokens(Date currentTime);
}
