/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rd.service;

import java.util.Map;

import com.rd.domain.User;
import com.rd.dto.ResponseWrapper;
import com.rd.dto.UserDTO;
import com.rd.dto.UserDetailDTO;

/**
 *
 * @author abhishek
 */
public interface UserService {
    public ResponseWrapper approveUser(String id,UserDetailDTO userDetailDTO);
    public ResponseWrapper inviteUser(User user);
    public ResponseWrapper listUser(UserDetailDTO userDetailDTO);
	public ResponseWrapper associateToOrganisation(String organisationId, String userId, UserDetailDTO userDetailDTO);
	public String createInitialUser(Map<String, Object> map, UserDetailDTO userDetailDTO,boolean apprve);
	public ResponseWrapper submitUser(String id, UserDetailDTO userDetailDTO);
	public ResponseWrapper addUser(UserDTO userDTO, UserDetailDTO userDetailDTO);
	public ResponseWrapper createUser(UserDTO userDTO);
	public ResponseWrapper getRolePermission(String roleName);
	public ResponseWrapper addRolePermission(String roleName, Map<String, Boolean> menus);
	public ResponseWrapper listUser(int status, UserDetailDTO userDetailDTO);
	public ResponseWrapper addClassificationToUser(String classificationId, UserDetailDTO userDetailDTO);
	public ResponseWrapper listAssociation(String type);
	public ResponseWrapper resetPassword(User user, String password);
	public ResponseWrapper createRole(Map<String, Object> input, UserDetailDTO userDetailDTO);
	public ResponseWrapper listHarvest(UserDetailDTO userDetailDTO);
	public ResponseWrapper syncHarvest(Long id, UserDetailDTO userDetailDTO);
}
