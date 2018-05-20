/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rd.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.rd.domain.Classfication;
import com.rd.domain.Organisation;
import com.rd.domain.User;
import com.rd.domain.UserRole;
import com.rd.dto.AddressDTO;
import com.rd.dto.OrganisationDTO;
import com.rd.dto.ResponseWrapper;
import com.rd.dto.UserDTO;
import com.rd.dto.UserDetailDTO;
import com.rd.dto.transaction.BriefRecord;
import com.rd.dto.transaction.Record;
import com.rd.dto.transaction.InsertResult;
import com.rd.dto.transaction.Response;
import com.rd.dto.transaction.Transaction;
import com.rd.repository.ClassificationRepository;
import com.rd.repository.OragnisationRepository;
import com.rd.repository.UserRepository;
import com.rd.utils.DTOConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author abhishek
 */
@Service
public class OrganisationServiceImpl implements OrganisationService {

	@Autowired
	private CommonService commonService;

	@Autowired
	private OragnisationRepository oragnisationRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;
	
	@Autowired
	private ClassificationRepository classificationRepository;

	// Association Id
	// {"transaction":[{"delete":["urn:uuid:b19a17fd-4dc1-4264-80ba-dbb2b826d487"]}]}

	@Override
	public ResponseWrapper approveOrganisation(Long id, UserDetailDTO userDetailDTO) {
		Organisation organisation = oragnisationRepository.findByIdAndStatus(id, 1);
		if (organisation != null) {
			int count = 0;
			List<User> users = userRepository.findByRegistryOrganisationIdAndStatus(organisation.getRegistryId(), 2);
			for (User user : users) {
				UserDTO userDTO = DTOConverter.getUserDTO(user);
				ObjectMapper oMapper = new ObjectMapper();
				@SuppressWarnings("unchecked")
				Map<String, Object> map = oMapper.convertValue(userDTO, Map.class);
				userDetailDTO.setOrganisationRegistryId(organisation.getRegistryId());
				String userId = userService.createInitialUser(map, userDetailDTO,true);
				count = count + 1;
				user.setRegistryId(userId);
				user.setStatus(3);
				userRepository.save(user);
			}
			if (count == 2) {
				ResponseWrapper responseWrapper = commonService.approve(organisation.getRegistryId(), "Organization",
						userDetailDTO);
				if (responseWrapper != null && responseWrapper.isSuccess()) {
					organisation.setStatus(2);
					oragnisationRepository.save(organisation);
				}
				return responseWrapper;
			}
			else{
				return new ResponseWrapper(false,"Initial Users require");
			}
		}
		return new ResponseWrapper(false,"No organisation found");
	}

	public ResponseWrapper addOrganisation(OrganisationDTO organisationDTO) {
		Organisation organisation = DTOConverter.getOrganisation(organisationDTO);

		List<User> userList = new ArrayList<User>();
		User nodalOfficcer = DTOConverter.getUser(organisationDTO.getNodalOfficer());
		nodalOfficcer.setUserRoles(Arrays.asList(new UserRole[] { new UserRole("NodalOfficer") }));
		userRepository.save(nodalOfficcer);
		userList.add(nodalOfficcer);

		User admin = DTOConverter.getUser(organisationDTO.getAdmin());
		admin.setUserRoles(Arrays.asList(new UserRole[] { new UserRole("Admin") }));
		userRepository.save(admin);
		userList.add(admin);
		organisation.setUsers(userList);
		oragnisationRepository.save(organisation);

		return new ResponseWrapper(true, "UserRegister Successfully");
	}

	@Override
	public ResponseWrapper createOrganisation(OrganisationDTO organisationDTO, UserDetailDTO userDetailDTO) {
		
		if(organisationDTO.getName() == null || organisationDTO.getNodalOfficer() == null || organisationDTO.getNodalOfficer().getEmailAddress() == null || organisationDTO.getAdmin() == null || organisationDTO.getAdmin().getEmailAddress() == null) {
		   return new ResponseWrapper(false, "Required Parameters cannot be null");	
		}
		
		List<Long> classificationIds = Arrays.asList(new Long[]{organisationDTO.getDataAccess()});
		List<String> classificationRegistryIds = new ArrayList<String>();
		List<Classfication> classfications = classificationRepository.findByIdInAndStatus(classificationIds,1);
		for(Classfication classfication:classfications){
			classificationRegistryIds.add(classfication.getRegistryId());
		}
		
		Response response = createOrganisationIndicio(organisationDTO, userDetailDTO);
		String organisationId = null;
		if (response != null && response.getInsertResults() != null) {
			for (InsertResult insertResult : response.getInsertResults()) {
				if (insertResult != null && insertResult.getBriefRecords() != null) {
					for (BriefRecord briefRecord : insertResult.getBriefRecords()) {
						if (briefRecord != null) {
							organisationId = briefRecord.getIdentifier();
							commonService.addClassification(organisationId,classificationRegistryIds, userDetailDTO);
						}
					}
				}
			}
		}
		if (organisationId != null) {
			Organisation organisation = new Organisation();
			organisation.setRegistryId(organisationId);
			organisation.setName(organisationDTO.getName());
			organisation.setStatus(1);
			organisation.setCreatedAt(new Date());
			organisation.setUpdatedAt(new Date());
			if (response != null) {
				List<User> users = new ArrayList<User>();
				UserDTO nodalOfficerDTO = organisationDTO.getNodalOfficer();
				if (nodalOfficerDTO != null) {
					User nodalOfficer = DTOConverter.getUser(nodalOfficerDTO);
					nodalOfficer.setRegistryOrganisationId(organisationId);
					nodalOfficer.setUserRoles(Arrays.asList(new UserRole[] { new UserRole("NodalOfficer") }));
					nodalOfficer = userRepository.save(nodalOfficer);
					users.add(nodalOfficer);
					userService.inviteUser(nodalOfficer);
				}
				UserDTO adminDTO = organisationDTO.getAdmin();
				if (adminDTO != null) {
					User admin = DTOConverter.getUser(adminDTO);
					admin.setRegistryOrganisationId(organisationId);
					admin.setUserRoles(Arrays.asList(new UserRole[] { new UserRole("Admin") }));
					admin = userRepository.save(admin);
					users.add(admin);
					userService.inviteUser(admin);
				}
				organisation.setUsers(users);
			}
			oragnisationRepository.save(organisation);
		}
		return new ResponseWrapper(response);
	}

	public Response createOrganisationIndicio(OrganisationDTO organisationDTO, UserDetailDTO userDetailDTO) {

		Record insert = new Record();
		insert.setId("Organization");
		insert.setObjectType("urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Organization");
		insert.setStatus("urn:oasis:names:tc:ebxml-regrep:StatusType:Submitted");

		List<Map<String, String>> names = new ArrayList<>();
		Map<String, String> name = new HashMap<String, String>();
		name.put("lang", "en");
		name.put("charset", "UTF-8");
		name.put("value", organisationDTO.getName());
		names.add(name);
		insert.setName(names);

		List<Map<String, String>> descriptions = new ArrayList<>();
		Map<String, String> description = new HashMap<String, String>();
		description.put("lang", "en");
		description.put("charset", "UTF-8");
		description.put("value", organisationDTO.getDescription());
		descriptions.add(description);
		insert.setDescription(descriptions);

		insert.setAddresses(organisationDTO.getAddress());
		insert.setEmailAddresses(organisationDTO.getEmailAddress());
		insert.setTelephoneNumbers(organisationDTO.getPhoneNumbers());

		List<Record> inserts = new ArrayList<Record>();
		inserts.add(insert);

		List<Map<String, Object>> transactions = new ArrayList<>();
		Map<String, Object> trMap = new HashMap<String, Object>();
		trMap.put("insert", inserts);
		transactions.add(trMap);

		Transaction transaction = new Transaction();
		transaction.setTransaction(transactions);

		return commonService.transaction(transaction, userDetailDTO);
	}

	@Override
	public ResponseWrapper listOrganisation() {
		List<Organisation> organisations = oragnisationRepository.findByStatus(2);
		List<OrganisationDTO> organisationDTOs = new ArrayList<>();
		for (Organisation organisation : organisations) {
			OrganisationDTO organisationDTO = new OrganisationDTO();
			organisationDTO.setId(organisation.getId());
			organisationDTO.setRegistryId(organisation.getRegistryId());
			organisationDTO.setName(organisation.getName());
			organisationDTOs.add(organisationDTO);
		}
		return new ResponseWrapper(organisationDTOs);
	}

	@Override
	public ResponseWrapper inactiveOrganisation(UserDetailDTO userDetailDTO) {
		List<Organisation> organisations = oragnisationRepository.findByStatusOrderByIdDesc(1);
		int requiredUser = 0;
		List<OrganisationDTO> organisationDTOs = new ArrayList<>();
		for (Organisation organisation : organisations) {
			OrganisationDTO organisationDTO = new OrganisationDTO();
			organisationDTO.setId(organisation.getId());
			organisationDTO.setRegistryId(organisation.getRegistryId());
			organisationDTO.setName(organisation.getName());
			List<User> users = organisation.getUsers();
			for (User user : users) {
				UserDTO userDTO = DTOConverter.getUserDTO(user);
				List<UserRole> userRoles = user.getUserRoles();
				if (userRoles.contains(new UserRole("NodalOfficer"))) {
					if (user.getStatus() == 2)
						requiredUser = requiredUser + 1;
					organisationDTO.setNodalOfficer(userDTO);
				} else if (userRoles.contains(new UserRole("Admin"))) {
					if (user.getStatus() == 2)
						requiredUser = requiredUser + 1;
					organisationDTO.setAdmin(userDTO);
				} else {
					organisationDTO.setUser(userDTO);
				}
			}
			organisationDTO.setUserRegistered(requiredUser);
			organisationDTOs.add(organisationDTO);
		}
		return new ResponseWrapper(organisationDTOs);
	}

}
