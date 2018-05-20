/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rd.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rd.domain.EmailVerification;
import com.rd.domain.Harvest;
import com.rd.domain.IdentityProof;
import com.rd.domain.RoleMenu;
import com.rd.domain.User;
import com.rd.domain.UserRole;
import com.rd.dto.HarvestDTO;
import com.rd.dto.ResponseWrapper;
import com.rd.dto.UserDTO;
import com.rd.dto.UserDetailDTO;
import com.rd.dto.transaction.BriefRecord;
import com.rd.dto.transaction.Classification;
import com.rd.domain.Classfication;
import com.rd.dto.transaction.InsertResult;
import com.rd.dto.transaction.Record;
import com.rd.dto.transaction.Response;
import com.rd.dto.transaction.Slot;
import com.rd.dto.transaction.Transaction;
import com.rd.repository.ClassificationRepository;
import com.rd.repository.EmailVerificationRepository;
import com.rd.repository.HarvestRepository;
import com.rd.repository.IdentityProofRepository;
import com.rd.repository.RoleMenuRepository;
import com.rd.repository.UserRepository;
import com.rd.repository.UserRoleRepository;
import com.rd.utils.Association;
import com.rd.utils.Constant;
import com.rd.utils.DTOConverter;
import com.rd.utils.ObjectType;
import com.rd.utils.SendEmail;
import com.rd.utils.ServiceType;
import com.rd.utils.StatusType;

/**
 *
 * @author abhishek
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserRoleRepository userRoleRepository;

	@Autowired
	private CommonService commonService;

	@Autowired
	private EmailVerificationRepository emailVerificationRepository;

	@Autowired
	private SendEmail sendEmail;

	@Autowired
	RoleMenuRepository roleMenuRepository;
	
	@Autowired
	private IdentityProofRepository identityProofRepository;

	@Autowired
	private ClassificationRepository classificationRepository;
	
	@Autowired
	HarvestRepository harvestRepository;

	@Override
	public ResponseWrapper approveUser(String id, UserDetailDTO userDetailDTO) {
		ResponseWrapper responseWrapper = new ResponseWrapper(false, "Something went wrong");
		if (id.indexOf("U-") > -1) {
			long userTableId = Long.parseLong(id.replace("U-", ""));
			User user = userRepository.findByIdAndStatus(userTableId, 2);
			if (user != null) {
				user.setStatus(3);
				
				try{
					sendEmail.sendEmailJavaAPI(user.getEmailAddress(), "Approval",
							"Your account has been approved.");
				}catch(Exception e){}
				
				userRepository.save(user);
				responseWrapper = commonService.approve(user.getRegistryId(), "User", userDetailDTO);
			}
		}
		return responseWrapper;
	}

	public ResponseWrapper listUser(UserDetailDTO userDetailDTO) {
		List<Integer> status = Arrays.asList(new Integer[] { 1, 2, 3 });
		List<User> users = userRepository
				.findByRegistryOrganisationIdAndStatusIn(userDetailDTO.getOrganisationRegistryId(), status);
		List<UserDTO> userDTOs = new ArrayList<UserDTO>();
		for (User user : users) {
			if (user.getStatus() > 0) {
				UserDTO userDTO = DTOConverter.getUserDTO(user);
				userDTOs.add(userDTO);
			}
		}
		return new ResponseWrapper(userDTOs);
	}

	public ResponseWrapper listUser(int status, UserDetailDTO userDetailDTO) {
		List<User> users = userRepository
				.findByRegistryOrganisationIdAndStatus(userDetailDTO.getOrganisationRegistryId(), status);
		List<UserDTO> userDTOs = new ArrayList<UserDTO>();
		for (User user : users) {
			if (user.getStatus() > 0) {
				UserDTO userDTO = DTOConverter.getUserDTO(user);
				userDTOs.add(userDTO);
			}
		}
		return new ResponseWrapper(userDTOs);
	}

	@Override
	public ResponseWrapper inviteUser(User user) {
		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString();
		EmailVerification emailVerification = new EmailVerification();
		emailVerification.setUniqueId(randomUUIDString);
		emailVerification.setServiceType(ServiceType.INVITE);
		emailVerification.setServiceId(user.getId());
		emailVerification.setStatus(1);
		emailVerification.setCreatedAt(new Date());
		emailVerification.setUpdatedAt(new Date());
		emailVerificationRepository.save(emailVerification);
		try {
			sendEmail.sendEmailJavaAPI(user.getEmailAddress(), "Invite",
					Constant.baseUrl + "/verify/acceptInvite/" + randomUUIDString);
		} catch (Exception ex) {
			System.out.println("Main can not be sent");
		}
		return new ResponseWrapper(true, "User Invite Successfull");
	}

	public ResponseWrapper activationLink(long id) {
		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString();
		EmailVerification emailVerification = new EmailVerification();
		emailVerification.setUniqueId(randomUUIDString);
		emailVerification.setServiceType(ServiceType.ACTIVATION);
		emailVerification.setServiceId(id);
		emailVerification.setStatus(1);
		emailVerification.setCreatedAt(new Date());
		emailVerification.setUpdatedAt(new Date());
		emailVerificationRepository.save(emailVerification);
		return new ResponseWrapper(true, "Activation Link");
	}

	public ResponseWrapper createUser(Map<String, Object> input, UserDetailDTO userDetailDTO) {
		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString();
		input.put("id", randomUUIDString);
		if (input.get("middleName") == null) {
			input.put("middleName", "");
		}
		if (input.get("password") == null) {
			input.put("password", "testing");
		}

		String output = commonService.operation("gcs", input, "createUser", userDetailDTO);
		return new ResponseWrapper(output);
	}

	public ResponseWrapper submitUser(String id, UserDetailDTO userDetailDTO) {
		ResponseWrapper responseWrapper = new ResponseWrapper(false, "Something went wrong");
		if (id.indexOf("U-") > -1) {
			long userTableId = Long.parseLong(id.replace("U-", ""));
			User user = userRepository.findByIdAndStatus(userTableId, 1);
			if (user != null) {
				UserDTO userDTO = DTOConverter.getUserDTO(user);
				ObjectMapper oMapper = new ObjectMapper();
				@SuppressWarnings("unchecked")
				Map<String, Object> map = oMapper.convertValue(userDTO, Map.class);
				String registryId = createInitialUser(map, userDetailDTO, false);
				if (registryId != null) {
					user.setStatus(2);
					user.setRegistryId(registryId);
					userRepository.save(user);
					responseWrapper = new ResponseWrapper(true, "User Successfully Submitted");
				} else {
					responseWrapper = new ResponseWrapper(false, "Something went wrong");
				}
			}
		}
		return responseWrapper;

	}

	public String createInitialUser(Map<String, Object> input, UserDetailDTO userDetailDTO, boolean approve) {
		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString();
		input.put("id", randomUUIDString);
		if (input.get("middleName") == null) {
			input.put("middleName", "");
		}
		// if (input.get("password") == null) {
		// input.put("password", input.get("password"));
		// }

		Response[] output = commonService.operationResponse("gcs", input, "createUser", userDetailDTO);
		if (output != null) {
			for (Response response : output) {
				if (response != null && response.getInsertResults() != null) {
					for (InsertResult insertResult : response.getInsertResults()) {
						if (insertResult != null && insertResult.getBriefRecords() != null) {
							for (BriefRecord briefRecord : insertResult.getBriefRecords()) {
								if (briefRecord != null) {
									String userId = briefRecord.getIdentifier();

									commonService.approve(userId, "User", userDetailDTO);

									associateToOrganisation(userDetailDTO.getOrganisationRegistryId(), userId,
											userDetailDTO);

									List<Slot> slots = new ArrayList<Slot>();
									Slot aSlot = new Slot();
									aSlot.setName("Aadhar");
									String aadhar = String.valueOf(input.get("aadhar"));
									aSlot.setValues(Arrays.asList(new String[] { aadhar }));
									slots.add(aSlot);

									Slot pSlot = new Slot();
									pSlot.setName("PAN");
									String pan = String.valueOf(input.get("pancard"));
									pSlot.setValues(Arrays.asList(new String[] { pan }));
									slots.add(pSlot);

									commonService.addSlots(userId, slots, userDetailDTO);

									return userId;
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public ResponseWrapper associateToOrganisation(String organisationId, String userId, UserDetailDTO userDetailDTO) {

		Record insert = new Record();
		insert.setObjectType(ObjectType.Association.toString());
		insert.setStatus(StatusType.Submitted.toString());

		insert.setAssociationType(Association.Employee.toString());
		insert.setSourceObject(organisationId);
		insert.setTargetObject(userId);

		List<Record> inserts = new ArrayList<Record>();
		inserts.add(insert);

		List<Map<String, Object>> transactions = new ArrayList<>();
		Map<String, Object> trMap = new HashMap<String, Object>();
		trMap.put("insert", inserts);
		transactions.add(trMap);

		Transaction transaction = new Transaction();
		transaction.setTransaction(transactions);

		return new ResponseWrapper(commonService.transaction(transaction, userDetailDTO));
	}

	@Override
	public ResponseWrapper addClassificationToUser(String classificationId, UserDetailDTO userDetailDTO) {
		List<Classification> classifications = new ArrayList<>();
		Classification classification = new Classification();
		classification.setClassificationNode(classificationId);
		classification.setClassifiedObject(userDetailDTO.getId());
		classification.setObjectType(ObjectType.Classification.toString());
		classifications.add(classification);
		return new ResponseWrapper(commonService.classification(userDetailDTO.getId(), classifications, userDetailDTO));
	}

	@Override
	public ResponseWrapper addUser(UserDTO userDTO, UserDetailDTO userDetailDTO) {
		userDTO.setRegistryOrganisationId(userDetailDTO.getOrganisationRegistryId());
		return createUser(userDTO);
	}

	public ResponseWrapper createUser(UserDTO userDTO) {

		if (userDTO.getLoginName() == null || userDTO.getFirstName() == null || userDTO.getLastName() == null
				|| userDTO.getPassword() == null) {
			return new ResponseWrapper(false, "Required Parameter cannot be null");
		}
		if (userDTO.getUserRole() == null) {
			userDTO.setUserRole("OrganisationUser");
		}
		
		IdentityProof identityProof = identityProofRepository.findByNameAndValue("AADHAR",userDTO.getAadhar());
		if(identityProof == null){
			return new ResponseWrapper(false, "Invalid Aadhar");
		}
		
		identityProof = identityProofRepository.findByNameAndValue("PAN",userDTO.getPancard());
		if(identityProof == null){
			return new ResponseWrapper(false, "Invalid PAN");
		}
		
		if (userDTO.getPassword() == null || userDTO.getPassword().length()<6)
			return new ResponseWrapper(false, "Password length should be 6 digit");

		List<Integer> status = Arrays.asList(new Integer[] { 0, 1, 2, 3 });
		User loginNameCheck = userRepository.findByLoginNameAndStatusIn(userDTO.getLoginName(), status);
		if (loginNameCheck != null)
			return new ResponseWrapper(false, "User Name already exist");

		User user = new User();
		user.setLoginName(userDTO.getLoginName());
		user.setPassword(userDTO.getPassword());
		user.setFirstName(userDTO.getFirstName());
		user.setMiddleName(userDTO.getMiddleName());
		user.setLastName(userDTO.getLastName());
		user.setAadhar(userDTO.getAadhar());
		user.setPancard(userDTO.getPancard());
		user.setEmailAddress(userDTO.getEmailAddress());
		user.setMobileNumber(userDTO.getMobileNumber());
		user.setRegistryOrganisationId(userDTO.getRegistryOrganisationId());
		user.setUserRoles(Arrays.asList(new UserRole[] { new UserRole(userDTO.getUserRole()) }));
		user.setStatus(0);

		user.setCreatedAt(new Date());
		user.setUpdatedAt(new Date());
		user = userRepository.save(user);

		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString();
		EmailVerification emailVerification = new EmailVerification();
		emailVerification.setUniqueId(randomUUIDString);
		emailVerification.setServiceType(ServiceType.ACTIVATION);
		emailVerification.setServiceId(user.getId());
		emailVerification.setStatus(1);
		emailVerification.setCreatedAt(new Date());
		emailVerification.setUpdatedAt(new Date());
		emailVerificationRepository.save(emailVerification);
		try {
			sendEmail.sendEmailJavaAPI(user.getEmailAddress(), "Register",
					Constant.baseUrl + "/emailVerify/" + randomUUIDString);
		} catch (Exception e) {
			System.out.println("Email cannot be sent");
		}
		return new ResponseWrapper(true, "Successfully Register");
	}

	public ResponseWrapper getRolePermission(String roleName) {
		Map<String, Boolean> menus = new HashMap<String, Boolean>();
		RoleMenu roleMenu = roleMenuRepository.findByRoleName(roleName);
		if (roleMenu != null) {
			for (String name : roleMenu.getMenuNames()) {
				menus.put(name, true);
			}
		}
		return new ResponseWrapper(menus);
	}

	public ResponseWrapper addRolePermission(String roleName, Map<String, Boolean> menus) {
		RoleMenu roleMenu = roleMenuRepository.findByRoleName(roleName);
		if (roleMenu == null) {
			roleMenu = new RoleMenu();
			roleMenu.setRoleName(roleName);
		}
		List<String> menuNames = new ArrayList<String>();
		for (String key : menus.keySet()) {
			if (menus.get(key)) {
				menuNames.add(key);
			}
		}
		roleMenu.setMenuNames(menuNames);
		roleMenuRepository.save(roleMenu);
		return new ResponseWrapper(true, "Updated Successfully");
	}

	@Override
	public ResponseWrapper listAssociation(String type) {
		List<Classfication> classifications = classificationRepository.findByTypeAndStatus(type, 1);
		return new ResponseWrapper(classifications);
	}

	public ResponseWrapper resetPassword(User user, String password) {
		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString();
		Map<String, Object> input = new HashMap<String, Object>();
		input.put("id", randomUUIDString);
		input.put("loginName", user.getLoginName());
		input.put("password", password);

		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities = AuthorityUtils.createAuthorityList("User");
		UserDetailDTO userDetailDTO = new UserDetailDTO(user.getLoginName(), user.getPassword(), authorities);
		String authString = userDetailDTO.getUsername() + ":" + userDetailDTO.getPassword();
		String token = Base64.getEncoder().encodeToString(authString.getBytes());
		userDetailDTO.setBasicAuth("Basic " + token);

		String output = commonService.operation("urm", input, "setPassword", userDetailDTO);
		return new ResponseWrapper(output);
	}

	public ResponseWrapper createRole(@RequestBody Map<String, Object> input, UserDetailDTO userDetailDTO) {
		String output = roleOnRegistry(input, userDetailDTO);
		if (output != null) {
			UserRole userRole = new UserRole();
			userRole.setRoleName((String) input.get("name"));
			userRole.setDescription((String) input.get("description"));
			userRoleRepository.save(userRole);
			return new ResponseWrapper(output);
		}
		return new ResponseWrapper(false, "Error occured in creating role");
	}

	public String roleOnRegistry(Map<String, Object> input, UserDetailDTO userDetailDTO) {
		String output = commonService.operation("urm", input, "createRole", userDetailDTO);
		Object data = input.get("menus");
		if (data != null) {
			List<String> menus = (List<String>) data;
			RoleMenu roleMenu = new RoleMenu();
			roleMenu.setRoleName((String) input.get("name"));
			roleMenu.setMenuNames(menus);
			roleMenuRepository.save(roleMenu);
		}
		return output;
	}

	public ResponseWrapper listHarvest(UserDetailDTO userDetailDTO) {
		List<Harvest> harvests = harvestRepository.findByOrganisationIdAndObjectTypeAndStatus(userDetailDTO.getOrganisationRegistryId(),ObjectType.ServiceProfile.toString(),1);
		List<HarvestDTO> harvestDTOs = new ArrayList<HarvestDTO>();		
		for(Harvest harvest:harvests){
			HarvestDTO harvestDTO = new HarvestDTO();
			harvestDTO.setId(harvest.getId());
			harvestDTO.setStoreNames(Arrays.asList(new String[]{harvest.getStoreName()}));
			harvestDTO.setNodeNames(Arrays.asList(new String[]{harvest.getNodeName()}));
			harvestDTO.setUpdateSequence(harvest.getUpdateSequence());
			harvestDTOs.add(harvestDTO);
		}
		return new ResponseWrapper(harvestDTOs);
	}

	public ResponseWrapper syncHarvest(Long harvestId,UserDetailDTO userDetailDTO) {
		return commonService.harvestSyncValidate(harvestId,userDetailDTO);
	}
}
