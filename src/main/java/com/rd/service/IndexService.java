/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rd.service;

import com.rd.domain.EmailVerification;
import com.rd.dto.LoginDTO;
import com.rd.dto.ResponseWrapper;
import com.rd.dto.UserDTO;
import com.rd.dto.UserDetailDTO;

/**
 *
 * @author abhishek
 */
public interface IndexService {

    public ResponseWrapper generateToken(LoginDTO loginDTO);
	public EmailVerification findByVerificationToken(String token);
	public ResponseWrapper activateUser(String token);
	public ResponseWrapper registerUser(UserDTO userDTO);
	public ResponseWrapper inviteUserAdd(String token, UserDTO userDTO);
	public ResponseWrapper recoverPassword(String token,UserDTO userDTO);
	public ResponseWrapper forgetPassword(String username);
	public ResponseWrapper otpVerify(String otp,LoginDTO loginDTO);
	ResponseWrapper generateToken(UserDetailDTO userDetailDTO);
}
