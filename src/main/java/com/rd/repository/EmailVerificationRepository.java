/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rd.repository;

import com.rd.domain.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author abhishek
 */
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

	EmailVerification findByUniqueIdAndStatus(String token, int i);
}

