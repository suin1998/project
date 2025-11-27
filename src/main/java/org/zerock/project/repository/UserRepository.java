package org.zerock.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.project.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUserIdAndActivatedTrue(String userId);

    Optional<User> findByUserId(String userId);

    Optional<User> findByEmail(String email);
}
