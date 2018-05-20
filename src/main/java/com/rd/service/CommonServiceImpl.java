/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rd.service;

import com.rd.dto.transaction.BriefRecord;
import com.rd.dto.transaction.Classification;
import com.rd.dto.transaction.InsertResult;
import com.rd.dto.transaction.Record;
import com.google.gson.Gson;
import com.rd.domain.Classfication;
import com.rd.domain.Harvest;
import com.rd.domain.Layer;
import com.rd.domain.LayerProperty;
import com.rd.domain.User;
import com.rd.dto.HarvestDTO;
import com.rd.dto.LayerDTO;
import com.rd.dto.PropertyDTO;
import com.rd.dto.ResponseWrapper;
import com.rd.dto.UserDTO;
import com.rd.dto.UserDetailDTO;
import com.rd.dto.transaction.Response;
import com.rd.dto.transaction.SearchResult;
import com.rd.dto.transaction.Slot;
import com.rd.dto.transaction.Transaction;
import com.rd.repository.ClassificationRepository;
import com.rd.repository.HarvestRepository;
import com.rd.repository.LayerPropertyRepository;
import com.rd.repository.LayerRepository;
import com.rd.repository.UserRepository;
import com.rd.utils.Association;
import com.rd.utils.Constant;
import com.rd.utils.GeoToolUtil;
import com.rd.utils.ObjectType;
import com.rd.utils.SlotDataType;
import com.rd.utils.SlotType;
import com.rd.utils.StatusType;

import static com.rd.utils.FreeMarker.getContent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author abhishek
 */
@Service
public class CommonServiceImpl implements CommonService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	HarvestRepository harvestRepository;

	@Autowired
	ClassificationRepository classificationRepository;

	@Autowired
	LayerRepository layerRepository;

	@Autowired
	LayerPropertyRepository layerPropertyRepository;

	Gson gson = new Gson();

	@Override
	public ResponseWrapper approve(String id, String type, UserDetailDTO userDetailDTO) {
		try {
			Map<String, Object> input = new HashMap<String, Object>();
			input.put("id", id);
			input.put("type", type);
			String content = getContent("approval.ftl", input);

			RestTemplate rest = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/xml");
			headers.add("Authorization", userDetailDTO.getBasicAuth());
			HttpEntity<?> requestEntity = new HttpEntity<>(content, headers);
			String url = Constant.url + "/gcs?&outputFormat=application/json";
			try {
				ResponseEntity<Object> responseEntity = rest.exchange(url, HttpMethod.POST, requestEntity,
						Object.class);
				return new ResponseWrapper(responseEntity.getBody());
			} catch (HttpClientErrorException e) {
				return new ResponseWrapper(false, e.getResponseBodyAsString());
			}
		} catch (Exception ex) {
			return new ResponseWrapper(false, ex.getMessage());
		}
	}

	@Override
	public String operation(String operationName, Map<String, Object> input, String template,
			UserDetailDTO userDetailDTO) {
		try {
			String content = getContent(template + ".ftl", input);
			System.out.println(content);
			RestTemplate rest = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/xml");
			headers.add("Authorization", userDetailDTO.getBasicAuth());
			HttpEntity<?> requestEntity = new HttpEntity<>(content, headers);
			String url = Constant.url + "/" + operationName + "?&outputFormat=application/json";
			try {
				ResponseEntity<String> responseEntity = rest.exchange(url, HttpMethod.POST, requestEntity,
						String.class);
				String response = responseEntity.getBody();
				return response;
			} catch (HttpClientErrorException e) {
				return e.getResponseBodyAsString();
			}
		} catch (Exception ex) {
			return ex.getMessage();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject search(String operationName, Map<String, Object> input, String template,
			UserDetailDTO userDetailDTO) {
		JSONObject response = null;
		try {
			String content = getContent(template + ".ftl", input);
			System.out.println(content);
			RestTemplate rest = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/xml");
			headers.add("Authorization", userDetailDTO.getBasicAuth());
			HttpEntity<?> requestEntity = new HttpEntity<>(content, headers);
			String url = Constant.url + "/" + operationName + "?&outputFormat=application/json";
			try {
				ResponseEntity<JSONObject> responseEntity = rest.exchange(url, HttpMethod.POST, requestEntity,
						JSONObject.class);
				// String response = responseEntity.getBody();
				response = responseEntity.getBody();
			} catch (HttpClientErrorException e) {
				response = new JSONObject();
				response.put("error", e.getMessage());

			}
		} catch (Exception ex) {
			response = new JSONObject();
			response.put("error", ex.getMessage());
		}
		return response;
	}

	@Override
	public Response[] operationResponse(String operationName, Map<String, Object> input, String template,
			UserDetailDTO userDetailDTO) {
		try {
			String content = getContent(template + ".ftl", input);
			System.out.println(content);
			RestTemplate rest = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/xml");
			headers.add("Authorization", userDetailDTO.getBasicAuth());
			HttpEntity<?> requestEntity = new HttpEntity<>(content, headers);
			String url = Constant.url + "/" + operationName + "?&outputFormat=application/json";
			try {
				ResponseEntity<Response[]> responseEntity = rest.exchange(url, HttpMethod.POST, requestEntity,
						Response[].class);
				Response[] response = responseEntity.getBody();
				return response;
			} catch (HttpClientErrorException e) {
				String message = e.getResponseBodyAsString();
				Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, e);
			}
		} catch (Exception ex) {
			Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	@Override
	public ResponseWrapper query(UserDetailDTO userDetailDTO, String request, String service, String version, String id,
			String outputFormat, String elementSetName, String qid, String resultType, String maxRecords, String type,
			String startPosition, String userId) {
		String query = "";
		if (request != null) {
			query = query + "&request=" + request;
		}
		if (service != null) {
			query = query + "&service=" + service;
		}
		if (version != null) {
			query = query + "&version=" + version;
		}
		if (id != null) {
			if (id.indexOf("U-") > -1) {
				long userTableId = Long.parseLong(id.replace("U-", ""));
				User user = userRepository.findOne(userTableId);
				if (user != null) {
					id = user.getRegistryId();
				}
			}
			query = query + "&id=" + id;
		}
		if (outputFormat != null) {
			query = query + "&outputFormat=" + outputFormat;
		}
		if (elementSetName != null) {
			query = query + "&elementSetName=" + elementSetName;
		}
		if (qid != null) {
			query = query + "&qid=" + qid;
		}
		if (resultType != null) {
			query = query + "&resultType=" + resultType;
		}
		if (maxRecords != null) {
			query = query + "&maxRecords=" + maxRecords;
		}
		if (startPosition != null) {
			query = query + "&startPosition=" + startPosition;
		}
		if (type != null) {
			query = query + "&type=" + type;
		}
		if (userId != null) {
			query = query + "&userId=" + userId;
		}
		try {

			RestTemplate rest = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", userDetailDTO.getBasicAuth());
			HttpEntity<?> requestEntity = new HttpEntity<>(headers);
			String url = Constant.url + "/query?" + query;
			try {
				ResponseEntity<Object> responseEntity = rest.exchange(url, HttpMethod.GET, requestEntity, Object.class);
				return new ResponseWrapper(responseEntity.getBody());
			} catch (HttpClientErrorException e) {
				return new ResponseWrapper(false, e.getResponseBodyAsString());
			}
		} catch (Exception ex) {
			Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
		}
		return new ResponseWrapper(false, "Something went wrong");
	}

	@Override
	public Response transaction(Transaction transaction, UserDetailDTO userDetailDTO) {
		try {
			Gson gson = new Gson();
			String data = gson.toJson(transaction);
			System.out.println(data);
			RestTemplate rest = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			headers.add("Authorization", userDetailDTO.getBasicAuth());
			HttpEntity<?> requestEntity = new HttpEntity<>(transaction, headers);
			String url = Constant.url + "/publish?&outputFormat=application/json";
			try {
				ResponseEntity<Response> responseEntity = rest.exchange(url, HttpMethod.POST, requestEntity,
						Response.class);
				Response response = responseEntity.getBody();
				return response;
			} catch (Exception e) {
				return null;
			}
		} catch (Exception ex) {
			Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResponseWrapper delete(List<String> ids, UserDetailDTO userDetailDTO) {
		try {

			JSONObject object = new JSONObject();
			object.put("delete", ids);
			JSONArray jSONArray = new JSONArray();
			jSONArray.add(object);

			JSONObject transaction = new JSONObject();
			transaction.put("transaction", jSONArray);

			RestTemplate rest = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			headers.add("Authorization", userDetailDTO.getBasicAuth());
			HttpEntity<?> requestEntity = new HttpEntity<>(transaction.toString(), headers);
			String url = Constant.url + "/publish?&outputFormat=application/json";
			try {
				ResponseEntity<String> responseEntity = rest.exchange(url, HttpMethod.POST, requestEntity,
						String.class);
				String response = responseEntity.getBody();
				return new ResponseWrapper(response);
			} catch (HttpClientErrorException e) {
				return new ResponseWrapper(false, e.getResponseBodyAsString());
			}
		} catch (Exception ex) {
			return new ResponseWrapper(false, ex.getMessage());
		}
	}

	public ResponseWrapper addSlots(String id, List<Slot> nSlots, UserDetailDTO userDetailDTO) {
		ResponseWrapper responseWrapper = new ResponseWrapper(false, "Uable to add slot");
		String query = "request=GetRecordById&service=CSW-ebRIM&version=1.0.1&id=" + id
				+ "&outputFormat=application/json&elementSetName=full";
		boolean newSlot = false;
		SearchResult searchResult = query(query, userDetailDTO);
		if (searchResult.getSearchResults() != null) {
			for (Record record : searchResult.getSearchResults()) {
				if (record != null) {
					List<Slot> slots = record.getSlots();
					if (slots == null)
						slots = new ArrayList<Slot>();
					slots.addAll(nSlots);
					record.setSlots(slots);
					newSlot = true;
				}
			}
		}
		if (newSlot) {
			Map<String, Object> update = new HashMap<String, Object>();
			update.put("update", searchResult.getSearchResults());
			List<Map<String, Object>> transactions = new ArrayList<Map<String, Object>>();
			transactions.add(update);
			Transaction transaction = new Transaction();
			transaction.setTransaction(transactions);
			Response response = transaction(transaction, userDetailDTO);
			if (response != null)
				responseWrapper = new ResponseWrapper(response);
		}
		return responseWrapper;

	}

	@Override
	public ResponseWrapper classification(String id, List<Classification> nClassifications,
			UserDetailDTO userDetailDTO) {
		ResponseWrapper responseWrapper = new ResponseWrapper(false, "Uable to add slot");
		Response response = classificationBase(id, nClassifications, userDetailDTO);
		if (response != null)
			responseWrapper = new ResponseWrapper(response);
		return responseWrapper;

	}

	public Response classificationBase(String id, List<Classification> nClassifications, UserDetailDTO userDetailDTO) {
		String query = "request=GetRecordById&service=CSW-ebRIM&version=1.0.1&id=" + id
				+ "&outputFormat=application/json&elementSetName=full";
		boolean newClassification = false;
		SearchResult searchResult = query(query, userDetailDTO);
		if (searchResult.getSearchResults() != null) {
			for (Record record : searchResult.getSearchResults()) {
				if (record != null) {
					List<Classification> classifications = record.getClassifications();
					if (classifications == null)
						classifications = new ArrayList<Classification>();
					classifications.addAll(nClassifications);
					record.setClassifications(classifications);
					newClassification = true;
				}
			}
		}
		if (newClassification) {
			Map<String, Object> update = new HashMap<String, Object>();
			update.put("update", searchResult.getSearchResults());
			List<Map<String, Object>> transactions = new ArrayList<Map<String, Object>>();
			transactions.add(update);
			Transaction transaction = new Transaction();
			transaction.setTransaction(transactions);
			Response response = transaction(transaction, userDetailDTO);
			return response;
		}
		return null;
	}

	public SearchResult query(String query, UserDetailDTO userDetailDTO) {
		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", userDetailDTO.getBasicAuth());
		HttpEntity<?> requestEntity = new HttpEntity<>(headers);
		String url = Constant.url + "/query?" + query;
		try {
			ResponseEntity<SearchResult> responseEntity = rest.exchange(url, HttpMethod.GET, requestEntity,
					SearchResult.class);
			return responseEntity.getBody();
		} catch (HttpClientErrorException e) {
			Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}

	public ResponseWrapper harvest(HarvestDTO harvestDTO, List<String> classificationIds, List<String> privateUserIds,
			UserDetailDTO userDetailDTO) throws Exception {
		String template = "harvest";
		Map<String, Object> input = new HashMap<String, Object>();
		String source = harvestDTO.getSource();
		source = source.replaceAll("&", "&amp;");
		input.put("source", source);
		input.put("resourceType", harvestDTO.getResourceType());
		String content = getContent(template + ".ftl", input);
		try {
			RestTemplate rest = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/xml");
			headers.add("Authorization", userDetailDTO.getBasicAuth());
			HttpEntity<?> requestEntity = new HttpEntity<>(content, headers);
			String url = Constant.url + "/publish?&outputFormat=application/json";
			try {
				ResponseEntity<Response> responseEntity = rest.exchange(url, HttpMethod.POST, requestEntity,
						Response.class);
				Response response = responseEntity.getBody();
				if (response != null && response.getInsertResults() != null) {
					for (InsertResult insertResult : response.getInsertResults()) {
						if (insertResult != null && insertResult.getBriefRecords() != null) {
							String serviceId = null;
							Map<String, String> dataSetObject = new HashMap<String, String>();
							for (BriefRecord briefRecord : insertResult.getBriefRecords()) {
								if (briefRecord != null) {
									if (briefRecord.getType().equals(ObjectType.ServiceProfile.toString())) {
										serviceId = briefRecord.getIdentifier();
									}
									if (briefRecord.getType().equals(ObjectType.DataSet.toString())) {
										String name = briefRecord.getTitle().get(0);
										name = name.substring(name.indexOf("}") + 1, name.length());
										String dataSetId = briefRecord.getIdentifier();
										dataSetObject.put(name, dataSetId);
									}
								}
							}
							
							List<Layer> layerLists = new ArrayList<>();
							List<Harvest> harvestList = new ArrayList<>();
							
							harvestList.add(addOtherDetail(serviceId, serviceId, ObjectType.ServiceProfile.toString(),ObjectType.ServiceProfile.toString(), harvestDTO, classificationIds,
									privateUserIds, userDetailDTO));
							
							Map<String, LayerDTO> layers = harvestDTO.getLayers();							
							for (String name : dataSetObject.keySet()) {
								harvestList.add(addOtherDetail(serviceId, dataSetObject.get(name), name,ObjectType.DataSet.toString(), harvestDTO, classificationIds,
										privateUserIds, userDetailDTO));
								LayerDTO layerDTO = layers.get(name);
								if(layerDTO != null){
									layerLists.add(layerData(dataSetObject.get(name), layerDTO));
								}
							}
							harvestRepository.save(harvestList);
							layerRepository.save(layerLists);
						}
					}
				}
				return new ResponseWrapper(response);
			} catch (Exception e) {
				return new ResponseWrapper(false, e.getMessage());
			}
		} catch (Exception ex) {
			Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
		}
		return new ResponseWrapper(false, "Something went wrong");
	}

	public Harvest addOtherDetail(String serviceId, String dataSetId, String layerName,String objectType, HarvestDTO harvestDTO,
			List<String> classificationIds, List<String> privateUserIds, UserDetailDTO userDetailDTO) {

		addSlot(dataSetId, harvestDTO, userDetailDTO);
		addClassification(dataSetId, classificationIds, userDetailDTO);

		List<String> associationIds = new ArrayList<String>();
		Response responseAsso = addAssociation(dataSetId, privateUserIds, userDetailDTO);
		if (responseAsso != null) {
			for (InsertResult insertResultAsso : responseAsso.getInsertResults()) {
				if (insertResultAsso != null && insertResultAsso.getBriefRecords() != null) {
					for (BriefRecord briefRecordAsso : insertResultAsso.getBriefRecords()) {
						associationIds.add(briefRecordAsso.getIdentifier());
					}
				}
			}
		}
		List<String> classificationLinkIds = classificationRegistryIds(serviceId, userDetailDTO);

		String harvestContent = gson.toJson(harvestDTO);
		Harvest harvest = new Harvest();
		// harvest.setId(1l);
		if (harvestDTO.getOrderId() != null) {
			harvest.setOrderId(harvestDTO.getOrderId());
		} else {
			harvest.setOrderId(0l);
		}
		harvest.setUpdateSequence(harvestDTO.getUpdateSequence());
		harvest.setUrl(harvestDTO.getSource());
		harvest.setOrganisationId(userDetailDTO.getOrganisationRegistryId());
		harvest.setHarvestId(serviceId);
		harvest.setDataId(dataSetId);
		harvest.setStoreName(harvestDTO.getStoreNames().get(0));
		harvest.setNodeName(harvestDTO.getNodeNames().get(0));
		harvest.setContent(harvestContent);
		
		harvest.setObjectType(objectType);
		harvest.setType("WFS");

		harvest.setClassificationIds(String.join(",", classificationLinkIds));
		harvest.setAssociationIds(String.join(",", associationIds));
		harvest.setStep(1);
		harvest.setStatus(1);
		harvest.setCreatedAt(new Date());
		harvest.setUpdatedAt(new Date());
		return harvest;
	}

	public Layer layerData(String dataSetId, LayerDTO layerDTO) {
		Layer layer = new Layer();
		layer.setDatasetId(dataSetId);
		layer.setName(layerDTO.getName());
		layer.setCount(layerDTO.getCount());
		if (layerDTO.getMetaData() != null)
			layer.setMetaData(gson.toJson(layerDTO.getMetaData()));
		List<LayerProperty> properties = new ArrayList<>();
		for (PropertyDTO propertyDTO : layerDTO.getProperties()) {
			LayerProperty property = new LayerProperty();
			property.setName(propertyDTO.getName());
			property.setDefaultValue(property.getDefaultValue());
			property.setType(property.getType());
			properties.add(property);
		}
		properties=layerPropertyRepository.save(properties);
		layer.setProperties(properties);
		layer.setStatus(1);
		layer.setCreatedAt(new Date());
		layer.setUpdatedAt(new Date());
		return layer;
	}

	public List<String> classificationRegistryIds(String id, UserDetailDTO userDetailDTO) {
		List<String> classificationLinkIds = new ArrayList<String>();
		String query = "request=GetRecordById&service=CSW-ebRIM&version=1.0.1&id=" + id
				+ "&outputFormat=application/json&elementSetName=full";
		SearchResult searchResult = query(query, userDetailDTO);
		if (searchResult.getSearchResults() != null) {
			for (Record record : searchResult.getSearchResults()) {
				if (record != null) {
					List<Classification> classifications = record.getClassifications();
					for (Classification classfication : classifications) {
						classificationLinkIds.add(classfication.getId());
					}

				}
			}
		}
		return classificationLinkIds;
	}

	public ResponseWrapper harvestValidate(HarvestDTO harvestDTO, UserDetailDTO userDetailDTO) throws Exception {
		if (harvestDTO.getSource() == null || harvestDTO.getDataAccess() == 0 || harvestDTO.getDataClassification() == 0
				|| harvestDTO.getPrice() == 0) {
			return new ResponseWrapper(false, "Required parameters are null");
		}
		List<Long> classificationIds = Arrays.asList(
				new Long[] { harvestDTO.getDataAccess(), harvestDTO.getDataClassification(), harvestDTO.getPrice() });
		List<String> classificationRegistryIds = new ArrayList<String>();
		List<Classfication> classfications = classificationRepository.findByIdInAndStatus(classificationIds, 1);
		for (Classfication classfication : classfications) {
			classificationRegistryIds.add(classfication.getRegistryId());
		}

		List<String> privateUserIds = null;
		if (harvestDTO.getUsers() != null && !harvestDTO.getUsers().isEmpty()) {
			List<Long> userIds = new ArrayList<>();
			for (UserDTO userDTO : harvestDTO.getUsers()) {
				userIds.add(userDTO.getId());
			}
			List<User> users = userRepository.findByIdInAndStatus(userIds, 3);
			privateUserIds = new ArrayList<>();
			for (User user : users) {
				privateUserIds.add(user.getRegistryId());
			}
		}

		if (harvestDTO.getUpdateSequence() == null) {

			Integer updateSequence = null;
			GeoToolUtil geoToolUtil = new GeoToolUtil(harvestDTO.getSource());
			geoToolUtil.importLayerToDB();
			Map<String, LayerDTO> layers = geoToolUtil.getLayers();
			// try {
			// updateSequence =
			// Integer.parseInt(metaData.get("updateSequence"));
			// } catch (Exception ex) {
			// return new ResponseWrapper(false, ex.getMessage());
			// }
			// harvestDTO.setUpdateSequence(updateSequence);
			harvestDTO.setLayers(layers);
			// harvestDTO.setMetaData(metaData);
		}
		return harvest(harvestDTO, classificationRegistryIds, privateUserIds, userDetailDTO);
	}

	public Response addClassification(String serviceId, List<String> classificationIds, UserDetailDTO userDetailDTO) {
		List<Classification> classifications = new ArrayList<>();
		for (String classificationId : classificationIds) {
			Classification classification = new Classification();
			classification.setClassificationNode(classificationId);
			classification.setClassifiedObject(serviceId);
			classification.setObjectType(ObjectType.Classification.toString());
			classifications.add(classification);
		}
		return classificationBase(serviceId, classifications, userDetailDTO);
	}

	public boolean addSlot(String serviceId, HarvestDTO harvestDTO, UserDetailDTO userDetailDTO) {
		List<Slot> slots = new ArrayList<Slot>();
		if (harvestDTO.getStoreNames() != null && !harvestDTO.getStoreNames().isEmpty()) {
			Slot storeSlot = new Slot();
			storeSlot.setName(SlotType.StoreName.toString());
			storeSlot.setSlotType(SlotDataType.String.toString());
			storeSlot.setValues(harvestDTO.getStoreNames());
			slots.add(storeSlot);
		}
		if (harvestDTO.getNodeNames() != null && !harvestDTO.getNodeNames().isEmpty()) {
			Slot nodeSlot = new Slot();
			nodeSlot.setName(SlotType.NodeName.toString());
			nodeSlot.setSlotType(SlotDataType.String.toString());
			nodeSlot.setValues(harvestDTO.getNodeNames());
			slots.add(nodeSlot);
		}

		if (harvestDTO.getCost() != null) {
			Slot nodeSlot = new Slot();
			nodeSlot.setName(SlotType.Cost.toString());
			nodeSlot.setSlotType(SlotDataType.String.toString());
			nodeSlot.setValues(Arrays.asList(new String[] { harvestDTO.getCost() }));
			slots.add(nodeSlot);
		}
		if (harvestDTO.getLocality() != null) {
			Slot nodeSlot = new Slot();
			nodeSlot.setName(SlotType.Locality.toString());
			nodeSlot.setSlotType(SlotDataType.String.toString());
			nodeSlot.setValues(Arrays.asList(new String[] { harvestDTO.getLocality() }));
			slots.add(nodeSlot);
		}
		if (harvestDTO.getYear() != null) {
			Slot nodeSlot = new Slot();
			nodeSlot.setName(SlotType.Year.toString());
			nodeSlot.setSlotType(SlotDataType.String.toString());
			nodeSlot.setValues(Arrays.asList(new String[] { harvestDTO.getYear() }));
			slots.add(nodeSlot);
		}
		addSlots(serviceId, slots, userDetailDTO);
		return true;
	}

	public Response addAssociation(String serviceId, List<String> privateUserIds, UserDetailDTO userDetailDTO) {
		List<Record> inserts = new ArrayList<Record>();
		// Organisation
		Record insert = new Record();
		insert.setObjectType(ObjectType.Association.toString());
		insert.setStatus(StatusType.Submitted.toString());
		insert.setAssociationType(Association.Owner.toString());
		insert.setSourceObject(serviceId);
		insert.setTargetObject(userDetailDTO.getOrganisationRegistryId());
		inserts.add(insert);
		// User
		insert = new Record();
		insert.setObjectType(ObjectType.Association.toString());
		insert.setStatus(StatusType.Submitted.toString());
		insert.setAssociationType(Association.Owner.toString());
		insert.setSourceObject(serviceId);
		insert.setTargetObject(userDetailDTO.getId());
		inserts.add(insert);
		// Private User
		if (privateUserIds != null) {
			for (String id : privateUserIds) {
				insert = new Record();
				insert.setObjectType(ObjectType.Association.toString());
				insert.setStatus(StatusType.Submitted.toString());
				insert.setAssociationType(Association.AccessControlPolicy.toString());
				insert.setSourceObject(serviceId);
				insert.setTargetObject(id);
				inserts.add(insert);
			}
		}
		List<Map<String, Object>> transactions = new ArrayList<>();
		Map<String, Object> trMap = new HashMap<String, Object>();
		trMap.put("insert", inserts);
		transactions.add(trMap);

		Transaction transaction = new Transaction();
		transaction.setTransaction(transactions);
		return transaction(transaction, userDetailDTO);
	}

	@Override
	public ResponseWrapper harvestSyncValidate(Long harvestId, UserDetailDTO userDetailDTO) {
		Gson gson = new Gson();
		Harvest harvest = harvestRepository.findByIdAndStatus(harvestId, 1);
		if (harvest != null) {

			Integer updateSequence = null;
			GeoToolUtil geoToolUtil = new GeoToolUtil(harvest.getUrl());
			geoToolUtil.importLayerToDB();
			Map<String, LayerDTO> layers = geoToolUtil.getLayers();
			//Map<String, String> metaData = geoToolUtil.fetchMetaData();

//			try {
//				updateSequence = Integer.parseInt(metaData.get("updateSequence"));
//			} catch (Exception ex) {
//				return new ResponseWrapper(false, ex.getMessage());
//			}
//			if (updateSequence == harvest.getUpdateSequence())
//				return new ResponseWrapper(false, "No update is required");

			List<String> ids = new ArrayList<>();
			if (harvest.getClassificationIds() != null) {
				String[] classificationIds = harvest.getClassificationIds().split(",");
				for (String classificationId : classificationIds) {
					ids.add(classificationId);
				}
			}
			if (harvest.getAssociationIds() != null) {
				String[] associationIds = harvest.getAssociationIds().split(",");
				for (String associationId : associationIds) {
					ids.add(associationId);
				}
			}
			
			List<Harvest> harvests = harvestRepository.findByHarvestIdAndStatus(harvest.getHarvestId(),1);
			List<Harvest> harvestUpdate = new ArrayList<>();
			List<String> dataSetIds = new ArrayList<>();
			for(Harvest harvestObj:harvests){
				dataSetIds.add(harvestObj.getDataId());
				harvestObj.setStatus(0);
				harvestUpdate.add(harvestObj);
			}
			List<Layer> layerList = layerRepository.findByDatasetIdInAndStatus(dataSetIds,1);
			List<Layer> layerListUpdate = new ArrayList<>();
			for(Layer layer:layerList){
				layer.setStatus(0);
				layerListUpdate.add(layer);
			}
			
			ResponseWrapper response = delete(ids, userDetailDTO);
			if (response != null && response.isSuccess()) {
				harvestRepository.save(harvestUpdate);
				layerRepository.save(layerListUpdate);

				HarvestDTO harvestDTO = gson.fromJson(harvest.getContent(), HarvestDTO.class);
				harvestDTO.setLayers(layers);
				// harvestDTO.setMetaData(metaData);
				if (harvest.getOrderId() == 0) {
					harvestDTO.setOrderId(harvest.getId());
				} else {
					harvestDTO.setOrderId(harvest.getOrderId());
				}
				harvestDTO.setUpdateSequence(updateSequence);
				try {
					return harvestValidate(harvestDTO, userDetailDTO);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return new ResponseWrapper(false, "Something went wrong");
	}

	public Integer getUpdateSequence(String url) {
		BufferedReader in = null;
		try {
			URL oracle = new URL(url);
			in = new BufferedReader(new InputStreamReader(oracle.openStream()));
			String inputLine = in.readLine();
			if (inputLine != null) {
				int pos = inputLine.indexOf("updateSequence") + ("updateSequence").length() + 2;
				String firstPart = inputLine.substring(pos, pos + 10);
				int endPos = firstPart.indexOf("\"");
				return Integer.parseInt(firstPart.substring(0, endPos));
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	
	
	public JSONObject getRecordById(String id,UserDetailDTO userDetailDTO){
		String query = "request=GetRecordById&service=CSW-ebRIM&version=1.0.1&id=" + id
				+ "&outputFormat=application/json&elementSetName=full";
		
		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", userDetailDTO.getBasicAuth());
		HttpEntity<?> requestEntity = new HttpEntity<>(headers);
		String url = Constant.url + "/query?" + query;
		try {
			ResponseEntity<JSONObject> responseEntity = rest.exchange(url, HttpMethod.GET, requestEntity,
					JSONObject.class);
			return responseEntity.getBody();
		} catch (HttpClientErrorException e) {
			Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}
	
	public Object getDatasetServiceProfile(String id,UserDetailDTO userDetailDTO){
		
		JSONArray response = new JSONArray();
		String query = "request=Query&service=CSW-ebRIM&version=1.0.1"
				+ "&qid=urn:x-indicio:def:stored-query:associated-tuples&resultType=results&outputFormat=application/json&id=" + id;
		
		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", userDetailDTO.getBasicAuth());
		HttpEntity<?> requestEntity = new HttpEntity<>(headers);
		String url = Constant.url + "/query?" + query;
		try {
			ResponseEntity<JSONObject> responseEntity = rest.exchange(url, HttpMethod.GET, requestEntity,
					JSONObject.class);
			JSONObject data =  responseEntity.getBody();
			 if(null != data && data.get("numberOfRecordsReturned") !=null && (int)data.get("numberOfRecordsReturned")>0){
				 ArrayList<HashMap<String,Object>> results = (ArrayList<HashMap<String,Object>>) ((ArrayList<HashMap<String,Object>>) data.get("searchResults"))
						 .get(0).get("registryObjectList");
				 for (Object object : results) {
					if (object != null) {
						String type = (String)((HashMap<String,Object>)object).get("objectType");
						if(type.equalsIgnoreCase("urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Service")){
							return object;							
						}
					}
				}
			}
		} catch (HttpClientErrorException e) {
			Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}

}
