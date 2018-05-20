/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rd.service;

import com.rd.dto.HarvestDTO;
import com.rd.dto.ResponseWrapper;
import com.rd.dto.UserDetailDTO;
import com.rd.dto.transaction.Classification;
import com.rd.dto.transaction.Response;
import com.rd.dto.transaction.Slot;
import com.rd.dto.transaction.Transaction;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 *
 * @author abhishek
 */
public interface CommonService {
    public ResponseWrapper approve(String id,String type, UserDetailDTO userDetailDTO);
    public String operation(String operationName, Map<String, Object> input,String template, UserDetailDTO userDetailDTO) ;
    public Response transaction(Transaction transaction, UserDetailDTO userDetailDTO);
    public ResponseWrapper delete(List<String> ids, UserDetailDTO userDetailDTO);
    public ResponseWrapper query(UserDetailDTO userDetailDTO,String request,String service,String version,String id,String outputFormat,String elementSetName,String qid,String resultType,String maxRecords,String type,String startPosition,String userId);
	Response[] operationResponse(String operationName, Map<String, Object> input, String template,
			UserDetailDTO userDetailDTO);
	public ResponseWrapper addSlots(String userId, List<Slot> slots, UserDetailDTO userDetailDTO);
	public ResponseWrapper harvestValidate(HarvestDTO harvestDTO, UserDetailDTO userDetailDTO) throws Exception;
	JSONObject search(String operationName, Map<String, Object> input, String template, UserDetailDTO userDetailDTO);
	ResponseWrapper classification(String id, List<Classification> classification, UserDetailDTO userDetailDTO);
	public Response addClassification(String organisationId, List<String> classificationRegistryIds,
			UserDetailDTO userDetailDTO);
	public ResponseWrapper harvestSyncValidate(Long harvestId, UserDetailDTO userDetailDTO);
	public Object getDatasetServiceProfile(String resultId, UserDetailDTO userDetailDTO);
}
