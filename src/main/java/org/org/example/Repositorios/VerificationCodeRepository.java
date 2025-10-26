package org.example.Repositorios;

import java.util.Optional;
import org.example.Models.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByTxId(String txId);
}