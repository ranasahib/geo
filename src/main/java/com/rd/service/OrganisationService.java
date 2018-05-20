/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rd.service;

import com.rd.dto.OrganisationDTO;
import com.rd.dto.ResponseWrapper;
import com.rd.dto.UserDetailDTO;

/**
 *
 * @author abhishek
 */
public interface OrganisationService {
    public ResponseWrapper approveOrganisation(Long id,UserDetailDTO userDetailDTO);
    public ResponseWrapper listOrganisation();    
    public ResponseWrapper addOrganisation(OrganisationDTO organisationDTO);    
    public ResponseWrapper createOrganisation(OrganisationDTO organisationDTO, UserDetailDTO userDetailDTO);
	public ResponseWrapper inactiveOrganisation(UserDetailDTO userDetailDTO);
}
