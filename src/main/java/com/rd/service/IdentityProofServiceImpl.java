package com.rd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rd.domain.IdentityProof;
import com.rd.dto.ResponseWrapper;
import com.rd.repository.IdentityProofRepository;

@Service
public class IdentityProofServiceImpl implements IdentityProofService{
    
	@Autowired
	private IdentityProofRepository identityProofRepository;

	@Override
    public ResponseWrapper addIdentityProof(IdentityProof identityProof){
		identityProofRepository.save(identityProof);
		return new ResponseWrapper(true,"Added Successfully");
	}
	
	@Override
    public ResponseWrapper checkIdentityProof(String name,String value){
		IdentityProof identityProof = identityProofRepository.findByNameAndValue(name,value);
		if(identityProof != null){
			return new ResponseWrapper(true,"Verified");
		}
		else{
			return new ResponseWrapper(false,"Not Verified");			
		}
	}
	
}
