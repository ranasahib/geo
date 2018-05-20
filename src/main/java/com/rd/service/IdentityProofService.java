package com.rd.service;

import com.rd.domain.IdentityProof;
import com.rd.dto.ResponseWrapper;

public interface IdentityProofService {

	ResponseWrapper addIdentityProof(IdentityProof identityProof);

	ResponseWrapper checkIdentityProof(String name, String value);

}
