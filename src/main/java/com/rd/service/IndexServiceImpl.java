/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rd.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.rd.domain.EmailVerification;
import com.rd.domain.IdentityProof;
import com.rd.domain.RoleMenu;
import com.rd.domain.User;
import com.rd.dto.LoginDTO;
import com.rd.dto.ResponseWrapper;
import com.rd.dto.TokenExtendDTO;
import com.rd.dto.UserDTO;
import com.rd.dto.UserDetailDTO;
import com.rd.repository.EmailVerificationRepository;
import com.rd.repository.IdentityProofRepository;
import com.rd.repository.RoleMenuRepository;
import com.rd.repository.UserRepository;
import com.rd.utils.Constant;
import com.rd.utils.RandomString;
import com.rd.utils.SendEmail;
import com.rd.utils.ServiceType;

/**
 *
 * @author abhishek
 */
@Service
public class IndexServiceImpl implements IndexService {

	@Autowired
	private AuthorizationServerEndpointsConfiguration configuration;
	@Autowired
	private EmailVerificationRepository emailVerificationRepository;
	@Autowired
	UserRepository userRepository;

	@Autowired
	UserService userService;

	@Autowired
	IdentityProofRepository identityProofRepository;

	@Autowired
	RoleMenuRepository roleMenuRepository;

	@Autowired
	SendEmail sendEmail;

	String resourceId = "geoportal";

	@Override
	public ResponseWrapper generateToken(LoginDTO loginDTO) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities = AuthorityUtils.createAuthorityList("User");
		UserDetailDTO userDetailDTO = new UserDetailDTO(loginDTO.getLoginName(), loginDTO.getPassword(), authorities);
		return generateToken(userDetailDTO);
	}

	@Override
	public ResponseWrapper generateToken(UserDetailDTO userDetailDTO) {
		LoginDTO loginDTO = new LoginDTO();
		loginDTO.setLoginName(userDetailDTO.getUsername());
		loginDTO.setPassword(userDetailDTO.getPassword());

//		ResponseWrapper responseWrapper = verifyLogin(loginDTO);
//		if (responseWrapper == null || !responseWrapper.isSuccess())
//			return responseWrapper;

		// (String) responseWrapper.getData();
		String data = "{\"message\":\"Pass\",\"userContexts\":[{  \"userID\":\"userID\", \"roleList\":[{\"name\":\"RegistryAdministrator\"}]}]}";

		JSONObject obj = new JSONObject(data);
		String success = obj.getString("message");
		if (success.equals("Pass")) {
			JSONArray jUserContexts = obj.getJSONArray("userContexts");
			if (jUserContexts.length() > 0) {
				String status = "Approved"; // userStatus(userId);
				if (!status.contains("Approved")) {
					return new ResponseWrapper(false, "Contact adminstrator");
				}
				
				String password = RandomString.getCode(6);
				
				List<Integer> statuses = Arrays.asList(new Integer[] { 1, 2, 3 });
				User user = userRepository.findByLoginNameAndStatusIn(userDetailDTO.getUsername(), statuses);				
				sendEmail.sendEmailJavaAPI(user.getEmailAddress(), "OTP", "Your otp is "+password);
				if (user != null) {
					user.setPassword(password);
					userRepository.save(user);
					return new ResponseWrapper(true, "Enter the otp");
				}
			}
		}
		return new ResponseWrapper(false, "Username or password is incorrect");
	}

	@Override
	public ResponseWrapper otpVerify(String otp,LoginDTO loginDTO) {
		
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities = AuthorityUtils.createAuthorityList("User");
		UserDetailDTO userDetailDTO = new UserDetailDTO(loginDTO.getLoginName(), loginDTO.getPassword(), authorities);
		
		List<Integer> statuses = Arrays.asList(new Integer[] { 1, 2, 3 });
		User user = userRepository.findByLoginNameAndStatusIn(userDetailDTO.getUsername(), statuses);
		if (user == null || !user.getPassword().equals(otp)) {
			return new ResponseWrapper(false, "OTP is incorrect");
		}

//		ResponseWrapper responseWrapper = verifyLogin(loginDTO);
//		if (responseWrapper == null || !responseWrapper.isSuccess())
//			return responseWrapper;

		String data = "{\"message\":\"Pass\",\"userContexts\":[{  \"userID\":\"userID\", \"roleList\":[{\"name\":\"RegistryAdministrator\"}]}]}";

		JSONObject obj = new JSONObject(data);
		String success = obj.getString("message");
		if (success.equals("Pass")) {
			JSONArray jUserContexts = obj.getJSONArray("userContexts");
			if (jUserContexts.length() > 0) {
				JSONObject jUserContext = jUserContexts.getJSONObject(0);
				String userId = jUserContext.getString("userID");

				JSONArray jRoleList = jUserContext.getJSONArray("roleList");
				List<String> roles = new ArrayList<String>();
				Map<String, Boolean> menus = new HashMap<String, Boolean>();
				for (int i = 0; i < jRoleList.length(); i++) {
					JSONObject jRole = jRoleList.getJSONObject(i);
					roles.add(jRole.getString("name"));
					RoleMenu roleMenu = roleMenuRepository.findByRoleName(jRole.getString("name"));
					if (roleMenu != null) {
						for (String name : roleMenu.getMenuNames()) {
							menus.put(name, true);
						}
					}
				}
				userDetailDTO.setMenus(menus);
				userDetailDTO.setId(userId);
				userDetailDTO.setRoles(roles);
				userDetailDTO.setStatus("Approved");

				// String authString = userDetailDTO.getUsername() + ":" +
				// userDetailDTO.getPassword();
				String token = "YWRtaW46U2xpdGh5VG92ZXM=";
				// Base64.getEncoder().encodeToString(authString.getBytes());
				userDetailDTO.setBasicAuth("Basic " + token);
				userDetailDTO.setOrganisationRegistryId("admin");

				user = userRepository.findByLoginNameAndStatusIn(userDetailDTO.getUsername(), statuses);
				if (user != null) {
					String organisationRegistryId = user.getRegistryOrganisationId();
					userDetailDTO.setOrganisationRegistryId(organisationRegistryId);
				}
				return getToken(userDetailDTO, loginDTO);
			}

		} else {
			return new ResponseWrapper(false, "Username or password is incorrect");
		}
		return new ResponseWrapper(false, "Something went wrong");
	}

	public ResponseWrapper verifyLogin(LoginDTO loginDTO) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("usercontext", loginDTO);
		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity<?> requestEntity = new HttpEntity<>(map, headers);
		String url = Constant.url + "/urm/validate";
		try {
			ResponseEntity<String> responseEntity = rest.exchange(url, HttpMethod.POST, requestEntity, String.class);
			String response = responseEntity.getBody();
			return new ResponseWrapper(response);
		} catch (Exception e) {
			return new ResponseWrapper(true, "Unable to connect indicio");
		}
	}

	public String userStatus(String id) {
		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity<?> requestEntity = new HttpEntity<>(headers);
		String url = Constant.url + "/query?&request=GetRecordById&service=WRS&version=1.0.1&id=" + id
				+ "&resultType=object&outputFormat=application/json";
		try {
			ResponseEntity<String> responseEntity = rest.exchange(url, HttpMethod.GET, requestEntity, String.class);
			String response = responseEntity.getBody();
			JSONObject obj = new JSONObject(response);
			JSONArray searchResults = obj.getJSONArray("searchResults");
			if (searchResults.length() > 0) {
				JSONObject searchResult = searchResults.getJSONObject(0);
				String status = searchResult.getString("status");
				return status;
			}
			return response;
		} catch (Exception e) {
			return "Unable to connect indicio";
		}
	}

	public ResponseWrapper getToken(UserDetailDTO userDetailDTO, LoginDTO loginDTO) {
		Map<String, String> authorizationParameters = new HashMap<>();
		authorizationParameters.put("username", loginDTO.getLoginName());
		authorizationParameters.put("password", loginDTO.getPassword());
		authorizationParameters.put("clientId", "rajithapp");
		authorizationParameters.put("clientSecret", "rajithapp");
		Map<String, Serializable> extensionProperties = new HashMap<String, Serializable>();
		boolean approved = true;
		Set<String> responseTypes = new HashSet<String>();
		responseTypes.add("code");
		List<String> scopes = new ArrayList<>();
		scopes.add("read");
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		OAuth2Request oauth2Request = new OAuth2Request(authorizationParameters,
				authorizationParameters.get("clientId"), authorities, approved, new HashSet<String>(scopes),
				new HashSet<String>(Arrays.asList(resourceId)), null, responseTypes, extensionProperties);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetailDTO,
				"N/A", authorities);
		OAuth2Authentication auth = new OAuth2Authentication(oauth2Request, authenticationToken);
		AuthorizationServerTokenServices tokenService = configuration.getEndpointsConfigurer().getTokenServices();
		OAuth2AccessToken token = tokenService.createAccessToken(auth);

		TokenExtendDTO tokenExtendDTO = new TokenExtendDTO();
		tokenExtendDTO.setToken(token.getValue());
		tokenExtendDTO.setRoles(userDetailDTO.getRoles());
		tokenExtendDTO.setMenus(userDetailDTO.getMenus());

		return new ResponseWrapper(tokenExtendDTO);
	}

	@Override
	public EmailVerification findByVerificationToken(String token) {
		return emailVerificationRepository.findByUniqueIdAndStatus(token, 1);
	}

	public ResponseWrapper registerUser(UserDTO userDTO) {
		return userService.createUser(userDTO);
	}

	@RequestMapping(value = {
			"/inviteUserAdd/{token}" }, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseWrapper inviteUserAdd(@PathVariable String token, @RequestBody UserDTO userDTO) {
		EmailVerification emailVerification = emailVerificationRepository.findByUniqueIdAndStatus(token, 1);
		ResponseWrapper responseWrapper = new ResponseWrapper(false, "Invalid token");
		if (emailVerification != null && emailVerification.getServiceType().equals(ServiceType.INVITE)) {
			User user = userRepository.findByIdAndStatus(emailVerification.getServiceId(), 1);

			User loginNameCheck = userRepository.findByLoginNameAndStatus(userDTO.getLoginName(), 1);
			if (loginNameCheck != null) {
				responseWrapper = new ResponseWrapper(false, "Login Name already exist");
			}

			IdentityProof identityProof = identityProofRepository.findByNameAndValue("AADHAR", userDTO.getAadhar());
			if (identityProof == null) {
				return new ResponseWrapper(false, "Invalid Aadhar");
			}

			identityProof = identityProofRepository.findByNameAndValue("PAN", userDTO.getPancard());
			if (identityProof == null) {
				return new ResponseWrapper(false, "Invalid PAN");
			}

			if (userDTO.getPassword() == null || userDTO.getPassword().length() < 6)
				return new ResponseWrapper(false, "Password length should be 6 digit");

			user.setLoginName(userDTO.getLoginName());
			user.setFirstName(userDTO.getFirstName());
			user.setMiddleName(userDTO.getMiddleName());
			user.setLastName(userDTO.getLastName());

			user.setPassword(userDTO.getPassword());

			user.setAadhar(userDTO.getAadhar());
			user.setPancard(userDTO.getPancard());
			user.setMobileNumber(userDTO.getMobileNumber());
			user.setStatus(2);

			user.setUpdatedAt(new Date());
			userRepository.save(user);

			emailVerification.setStatus(0);
			emailVerification.setUpdatedAt(new Date());
			emailVerificationRepository.save(emailVerification);
			responseWrapper = new ResponseWrapper(true, "User successfully added");
		}
		return responseWrapper;
	}

	public ResponseWrapper activateUser(String token) {
		EmailVerification emailVerification = emailVerificationRepository.findByUniqueIdAndStatus(token, 1);
		if (emailVerification != null) {
			User user = userRepository.findByIdAndStatus(emailVerification.getServiceId(), 0);
			user.setStatus(1);
			userRepository.save(user);
			emailVerification.setStatus(0);
			emailVerification.setUpdatedAt(new Date());
			emailVerificationRepository.save(emailVerification);
			return new ResponseWrapper(true, "Email verified successfully");
		}
		return new ResponseWrapper(false, "Invalid Token");

	}

	@Override
	public ResponseWrapper forgetPassword(String username) {
		User user = userRepository.findByLoginNameAndStatus(username, 3);
		if (user != null) {
			UUID uuid = UUID.randomUUID();
			String randomUUIDString = uuid.toString();
			EmailVerification emailVerification = new EmailVerification();
			emailVerification.setUniqueId(randomUUIDString);
			emailVerification.setServiceType(ServiceType.FORGOT_PASSWORD);
			emailVerification.setServiceId(user.getId());
			emailVerification.setStatus(1);
			emailVerification.setCreatedAt(new Date());
			emailVerification.setUpdatedAt(new Date());
			emailVerificationRepository.save(emailVerification);
			try {
				sendEmail.sendEmailJavaAPI(user.getEmailAddress(), "Recover Password",
						Constant.baseUrl + "/verify/recoverPassword/" + randomUUIDString);
			} catch (Exception e) {
				new ResponseWrapper(false, "Email cannot be sent");
			}
		}
		return new ResponseWrapper(false, "User not found");
	}

	@Override
	public ResponseWrapper recoverPassword(String token, UserDTO userDTO) {
		EmailVerification emailVerification = emailVerificationRepository.findByUniqueIdAndStatus(token, 1);
		if (emailVerification != null && emailVerification.getServiceType().equals(ServiceType.FORGOT_PASSWORD)) {
			User user = userRepository.findByIdAndStatus(emailVerification.getServiceId(), 3);
			ResponseWrapper responseWrapper = userService.resetPassword(user, userDTO.getPassword());
			if (responseWrapper != null && responseWrapper.isSuccess()) {
				user.setPassword(userDTO.getPassword());
				userRepository.save(user);
				emailVerification.setStatus(0);
				emailVerification.setUpdatedAt(new Date());
				emailVerificationRepository.save(emailVerification);
				return new ResponseWrapper(true, "Password Reset Successfully");
			} else
				return responseWrapper;
		}
		return new ResponseWrapper(false, "Invalid Token");
	}

}
