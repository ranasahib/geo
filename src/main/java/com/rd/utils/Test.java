package com.rd.utils;

import java.util.Base64;

public class Test {

	public static void main(String[]args){
		String key = "Authorization:Basic YWRtaW46U2xpdGh5VG92ZXM=";
		
		String authString = "user1:test@1234";
		String encoding = Base64.getEncoder().encodeToString(authString.getBytes());

		System.out.println(encoding);
		
	}
}
