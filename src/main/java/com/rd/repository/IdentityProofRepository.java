package com.rd.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rd.domain.EmailVerification;
import com.rd.domain.IdentityProof;


public interface IdentityProofRepository extends JpaRepository<IdentityProof, Long> {

	IdentityProof findByNameAndValue(String name, String value);

}

