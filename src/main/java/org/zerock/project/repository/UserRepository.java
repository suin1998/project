package org.zerock.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.zerock.project.entity.User;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);


    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);


    @Query("SELECT u FROM User u WHERE u.email = :email AND u.enabled = true")
    Optional<User> findActiveUserByEmail(@Param("email") String email);

    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :loginTime WHERE u.id = :userId")
    void updateLastLoginTime(
            @Param("userId") Long userId,
            @Param("loginTime") LocalDateTime loginTime
    );

    @Modifying
    @Query("UPDATE User u SET u.emailVerified = true, " +
            "u.emailVerificationToken = null, " +
            "u.emailVerificationExpiry = null " +
            "WHERE u.id = :userId")
    void verifyUserEmail(@Param("userId") Long userId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startDate")
    Long countUsersCreatedAfter(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT u FROM User u WHERE u.emailVerified = false " +
            "AND u.createdAt < :beforeDate")
    java.util.List<User> findUnverifiedUsersBefore(
            @Param("beforeDate") LocalDateTime beforeDate
    );
}