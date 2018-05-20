/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rd.encryption;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;

import com.amazonaws.util.Base64;

/**
 *
 * @author abhishek
 */
public class DataConverter implements AttributeConverter<String, String> {

	private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
	private static final byte[] KEY = "MySuperSecretKey".getBytes();

	@Override
	public String convertToDatabaseColumn(String attribute) {
		if(attribute == null)
			return attribute;
		Key key = new SecretKeySpec(KEY, "AES");
	      try {
	         Cipher c = Cipher.getInstance(ALGORITHM);
	         c.init(Cipher.ENCRYPT_MODE, key);
	         return Base64.encodeAsString(c.doFinal(attribute.getBytes()));
	      } catch (Exception e) {
	         throw new RuntimeException(e);
	      }
	      
	}

	@Override
	public String convertToEntityAttribute(String dbData) {
		if(dbData == null)
			return dbData;
		Key key = new SecretKeySpec(KEY, "AES");
	      try {
	        Cipher c = Cipher.getInstance(ALGORITHM);
	        c.init(Cipher.DECRYPT_MODE, key);
	        return new String(c.doFinal(Base64.decode(dbData)));
	      } catch (Exception e) {
	        throw new RuntimeException(e);
	      }
	}
}