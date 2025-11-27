package org.zerock.project.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.project.entity.VerificationCode;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    Optional<VerificationCode> findByUserIdAndCodeAndExpiryDateAfterAndVerifiedFalse(
            String userId,
            String code,
            LocalDateTime now
    );
}
