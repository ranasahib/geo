/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rd.service;

import java.util.Map;

import org.json.simple.JSONObject;

import com.rd.dto.ResponseWrapper;
import com.rd.dto.UserDetailDTO;

/**
 *
 * @author abhishek
 */
public interface QueryService {

	
	JSONObject query(Map<String, Object> input, UserDetailDTO user);

	JSONObject advanceQuery(Map<String, Object> queryDto, UserDetailDTO userDetailDTO);

	ResponseWrapper search(Map<String, Object> queryDto, UserDetailDTO userDetailDTO, String type);

	
}
