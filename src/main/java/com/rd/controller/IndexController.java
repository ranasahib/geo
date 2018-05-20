package com.rd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.rd.domain.EmailVerification;
import com.rd.domain.IdentityProof;
import com.rd.dto.LoginDTO;
import com.rd.dto.ResponseWrapper;
import com.rd.dto.UserDTO;
import com.rd.service.IdentityProofService;
import com.rd.service.IndexService;
import com.rd.service.OrganisationService;

@Controller
@RequestMapping("/")
public class IndexController {

	@Autowired
	private IndexService indexService;
	@Autowired
	private IdentityProofService identityProofService;
	@Autowired
	private OrganisationService organisationService;

	@RequestMapping(value = { "/" })
	public String index() {
		return "index";
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = { "/token" }, method = RequestMethod.POST)
	public ResponseEntity<?> token(@RequestBody LoginDTO loginDTO) {
		ResponseWrapper responseWrapper = indexService.generateToken(loginDTO);
		HttpHeaders headers = new HttpHeaders();
		headers.set("result", "success");
		return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = { "/verifyotp/{otp}" }, method = RequestMethod.POST)
	public ResponseEntity<?> verifyotp(@PathVariable String otp,@RequestBody LoginDTO loginDTO) {
		ResponseWrapper responseWrapper = indexService.otpVerify(otp, loginDTO);
		HttpHeaders headers = new HttpHeaders();
		headers.set("result", "success");
		return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
	}

	@RequestMapping("/verify/{type}/{token}")
	public String checkToken(@PathVariable String type, @PathVariable String token) {
		EmailVerification emailVerification = indexService.findByVerificationToken(token);
		if (emailVerification != null) {
			if (type.equals("acceptInvite")) {
				return "redirect:" + "/#/acceptInvitation/" + token;
			} else if (type.equals("recoverPassword")) {
				return "redirect:" + "/#/recoverPassword/" + token;
			} else {
				return "invalidToken";
			}
		} else {
			return "invalidToken";
		}
	}

	@RequestMapping("/organisation/list")
	public ResponseEntity<?> organisationList() {
		ResponseWrapper responseWrapper = organisationService.listOrganisation();
		HttpHeaders headers = new HttpHeaders();
		headers.set("result", "success");
		return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
	}

	@RequestMapping(value = {
			"/inviteUserAdd/{token}" }, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> inviteUserAdd(@PathVariable String token, @RequestBody UserDTO userDTO) {
		ResponseWrapper responseWrapper = indexService.inviteUserAdd(token, userDTO);
		HttpHeaders headers = new HttpHeaders();
		headers.set("result", "success");
		return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
	}

	@RequestMapping(value = { "/register" }, method = RequestMethod.POST)
	public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
		ResponseWrapper responseWrapper = indexService.registerUser(userDTO);
		HttpHeaders headers = new HttpHeaders();
		headers.set("result", "success");
		return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
	}

	@RequestMapping("/emailVerify/{token}")
	public String emailVerify(@PathVariable String token) {
		ResponseWrapper responseWrapper = indexService.activateUser(token);
		if (responseWrapper.isSuccess()) {
			return "redirect:" + "/#/emailVerification";
		} else {
			return "invalidToken";
		}
	}

	@RequestMapping(value = {
			"/forgotPassword/{username}" }, method = RequestMethod.POST)
	public ResponseEntity<?> forgotPassword(@PathVariable String username) {
		ResponseWrapper responseWrapper = indexService.forgetPassword(username);
		HttpHeaders headers = new HttpHeaders();
		headers.set("result", "success");
		return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
	}

	@RequestMapping(value = {
			"/recoverPassword/{token}" }, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> recoverPassword(@PathVariable String token, @RequestBody UserDTO userDTO) {
		ResponseWrapper responseWrapper = indexService.recoverPassword(token, userDTO);
		HttpHeaders headers = new HttpHeaders();
		headers.set("result", "success");
		return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
	}

	@RequestMapping(value = {
			"/addIdentityProof" }, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addIdentityProof(@RequestBody IdentityProof identityProof) {		
		ResponseWrapper responseWrapper = identityProofService.addIdentityProof(identityProof);
		HttpHeaders headers = new HttpHeaders();
		headers.set("result", "success");
		return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
	}

	@RequestMapping(value = {
			"/validateIdentityProof/{name}/{value}" }, method = RequestMethod.GET)
	public ResponseEntity<?> validateIdentityProof(@PathVariable String name,@PathVariable String value) {		
		ResponseWrapper responseWrapper = identityProofService.checkIdentityProof(name, value);
		HttpHeaders headers = new HttpHeaders();
		headers.set("result", "success");
		return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
	}

}
