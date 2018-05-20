package com.rd.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rd.domain.Harvest;
import com.rd.domain.Layer;
import com.rd.dto.ResponseWrapper;
import com.rd.dto.UserDetailDTO;
import com.rd.repository.HarvestRepository;
import com.rd.repository.LayerRepository;
/**
*
* @author abhishek
*/
@Service
public class QueryServiceImpl implements QueryService{

	@Autowired
	private CommonService commonService;
	

	@Autowired
	LayerRepository layerRepository;
	
	@Autowired
	HarvestRepository harvestRepository;
	
	@Override
	public JSONObject query(Map<String, Object> input,UserDetailDTO user) {
		
		input.put("public", "publicid");
		input.put("protected", "protectedid");
		input.put("private", "privateid");
		
		input.put("userId", "userId");
		input.put("associationTypePrivate", "associationTypePrivate");
		input.put("organizationId", "organizationId");
		input.put("associationTypeProtected", "associationTypeProtected");
		
		input.put("defence", false);
		

		JSONObject output = commonService.search("query", input, "query",user);
		return output;
	}
	
	@Override
	public JSONObject advanceQuery(Map<String, Object> input,UserDetailDTO user) {
		
		input.put("public", "publicid");
		input.put("protected", "protectedid");
		input.put("private", "privateid");
		
		input.put("userId", "userId");
		input.put("associationTypePrivate", "associationTypePrivate");
		input.put("organizationId", "organizationId");
		input.put("associationTypeProtected", "associationTypeProtected");
		
		input.put("defence", false);
		
		String queryXml = (String) input.get("queryXml");
		if(null != queryXml){
			queryXml = queryXml.replaceAll("<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\">", "");
			queryXml = queryXml.replaceAll("</ogc:Filter>", "");
		}
		input.put("queryXml", queryXml);
		
		JSONObject output = commonService.search("query", input, "query",user);
		return output;
	}

	@Override
	public ResponseWrapper search(Map<String, Object> queryDto, UserDetailDTO userDetailDTO,String type) {
		JSONObject response = new JSONObject();
		JSONArray searchResults = new JSONArray();
		JSONObject data = null;
		 if(type.equalsIgnoreCase("simple")){
			 	data = query(queryDto,userDetailDTO);
	        }else{
	        	data = advanceQuery(queryDto,userDetailDTO);
	        }
		 
		 if(null != data && data.get("numberOfRecordsReturned") !=null && (int)data.get("numberOfRecordsReturned")>0){
			 response.put("Result", (int)data.get("numberOfRecordsReturned")+" records found.");
			 ArrayList<JSONObject> results = (ArrayList<JSONObject>) data.get("searchResults");
			 for (Object object : results) {
				 JSONObject resultObject = new JSONObject();
				HashMap<String,Object> map = (HashMap<String,Object>)object;
				String resultId = (String)map.get("id");
				resultObject.put("ID", resultId);
				
				List<Harvest> harvests = harvestRepository.findByHarvestIdAndStatus(resultId,1);				
				if (harvests != null && harvests.size() > 0) {
					Harvest harvest = harvests.get(0);
					resultObject.put("updatedAt", harvest.getUpdatedAt().getTime());

					Layer layer = layerRepository.findByDatasetIdAndStatus(harvest.getDataId(), 1);
					if (layer != null) {
						resultObject.put("count", layer.getCount());
					}
				}
				resultObject.put("slots", map.get("slots"));
				
				
				resultObject.put("slots", map.get("slots"));
				resultObject.put("name",(String)((HashMap<String,Object>)((ArrayList)map.get("name")).get(0)).get("value"));
				JSONObject serviceObject = fetchServiceObject(resultId,userDetailDTO);
				if(null != serviceObject){
					resultObject.put("serviceObject", serviceObject);
				}
				searchResults.add(resultObject);
			}
		 }else{
			 response.put("Result", "No records found.");
		 }
		 
		 response.put("searchResults", searchResults);
		return new ResponseWrapper(response);
	}

	private JSONObject fetchServiceObject(String resultId,UserDetailDTO userDetailDTO) {
		JSONObject serviceObject = null; 
		HashMap<String,Object> data = (HashMap<String,Object>)commonService.getDatasetServiceProfile(resultId,userDetailDTO);
		if(null != data){
			serviceObject = new JSONObject();
			serviceObject.put("id", data.get("id"));
			if(data.get("name")!=null){
				serviceObject.put("name", (String)((HashMap<String,Object>)((ArrayList)data.get("name")).get(0)).get("value"));
				
			}
			if(data.get("description")!=null){
				serviceObject.put("description", (String)((HashMap<String,Object>)((ArrayList)data.get("description")).get(0)).get("value"));
				
			}
			if(data.get("versionInfo")!=null){
				serviceObject.put("versionInfo", (String)((HashMap<String,Object>)data.get("versionInfo")).get("value"));
				
			}
			if(data.get("serviceBindings")!=null){
				serviceObject.put("accessURI", (String)((HashMap<String,Object>)((ArrayList)data.get("serviceBindings")).get(0)).get("accessURI"));
				
			}
			
			if(data.get("classifications")!=null){
				serviceObject.put("serviceType", (String)((HashMap<String,Object>)((ArrayList)data.get("classifications")).get(0)).get("classificationNode"));
				
			}
					
		}
		return serviceObject;
	}
		
}
