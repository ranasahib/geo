/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rd.utils;

import com.rd.domain.Address;
import com.rd.domain.EmailAddress;
import com.rd.domain.Organisation;
import com.rd.domain.PhoneNumber;
import com.rd.domain.User;
import com.rd.dto.AddressDTO;
import com.rd.dto.EmailAddressDTO;
import com.rd.dto.OrganisationDTO;
import com.rd.dto.PhoneNumberDTO;
import com.rd.dto.UserDTO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author abhishek
 */
public class DTOConverter {

    public static Organisation getOrganisation(OrganisationDTO organisationDTO) {
        Organisation organisation = new Organisation();
        organisation.setName(organisationDTO.getName());
        organisation.setDescription(organisationDTO.getDescription());
//        if (organisationDTO.getEmailAddress() != null) {
//            List<EmailAddress> emailAdresss = new ArrayList<EmailAddress>();
//            for (EmailAddressDTO emailAddressDTO : organisationDTO.getEmailAddress()) {
//                emailAdresss.add(getEmailAddress(emailAddressDTO));
//            }
//            organisation.setEmailAddress(emailAdresss);
//        }
//        if (organisationDTO.getPhoneNumbers() != null) {
//            List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
//            for (PhoneNumberDTO phoneNumberDTO : organisationDTO.getPhoneNumbers()) {
//                phoneNumbers.add(getPhoneNumber(phoneNumberDTO));
//            }
//            organisation.setPhoneNumbers(phoneNumbers);
//        }
//        if (organisationDTO.getAddress() != null) {
//            List<Address> addresses = new ArrayList<Address>();
//            for (AddressDTO addressDTO : organisationDTO.getAddress()) {
//                addresses.add(getAddress(addressDTO));
//            }
//            organisation.setAddress(addresses);
//        }
        organisation.setStatus(1);
        organisation.setCreatedAt(new Date());
        organisation.setUpdatedAt(new Date());
        return organisation;
    }

    public static User getUser(UserDTO userDTO) {
        User user = new User();
        user.setLoginName(userDTO.getLoginName());
        user.setFirstName(userDTO.getFirstName());
        user.setMiddleName(userDTO.getMiddleName());
        user.setLastName(userDTO.getLastName());
        user.setAadhar(userDTO.getAadhar());
        user.setPancard(userDTO.getPancard());

        user.setEmailAddress(userDTO.getEmailAddress());
        user.setMobileNumber(userDTO.getMobileNumber());
        
        user.setRegistryOrganisationId(userDTO.getRegistryOrganisationId());
        
        user.setStatus(1);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        return user;
    }

    public static Address getAddress(AddressDTO addressDTO) {
        Address address = new Address();
        address.setCity(addressDTO.getCity());
        address.setCountry(addressDTO.getCountry());
        address.setPostalCode(addressDTO.getPostalCode());
        address.setStateOrProvince(addressDTO.getStateOrProvince());
        address.setStreet(addressDTO.getStreet());
        address.setStreetNumber(addressDTO.getStreetNumber());
        address.setAddressType(addressDTO.getAddressType());
        return address;
    }

    public static EmailAddress getEmailAddress(EmailAddressDTO emailAddressDTO) {
        EmailAddress emailAdress = new EmailAddress();
        emailAdress.setRegistryId(emailAddressDTO.getRegistryId());
        emailAdress.setEmail(emailAddressDTO.getEmail());
        emailAdress.setEmailType(emailAddressDTO.getEmailType());
        return emailAdress;
    }

    public static PhoneNumber getPhoneNumber(PhoneNumberDTO phoneNumberDTO) {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setCountryCode(phoneNumberDTO.getCountryCode());
        phoneNumber.setRegistryId(phoneNumberDTO.getRegistryId());
        phoneNumber.setNumber(phoneNumberDTO.getNumber());
        phoneNumber.setPhoneType(phoneNumberDTO.getPhoneType());
        return phoneNumber;
    }

	public static UserDTO getUserDTO(User user) {
		UserDTO userDTO = new UserDTO();
		userDTO.setId(user.getId());
        userDTO.setLoginName(user.getLoginName());
        userDTO.setPassword(user.getPassword());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setMiddleName(user.getMiddleName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmailAddress(user.getEmailAddress());
        userDTO.setMobileNumber(user.getMobileNumber());
        userDTO.setAadhar(user.getAadhar());
        userDTO.setPancard(user.getPancard());
        if(user.getUserRoles() != null && user.getUserRoles().size() > 0)
          userDTO.setUserRole(user.getUserRoles().get(0).getRoleName());
        userDTO.setStatus(user.getStatus());
		return userDTO;
	}
}
