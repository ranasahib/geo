package com.rd.utils;

import java.util.Random;

public class RandomString {
	private char[] symbols;
	private Random random;
	private int length;
	
	public RandomString(int length){
		if(length < 1)
		    throw new IllegalArgumentException("Length should be greater than "+length);
		
		this.length = length;		
		this.random = new Random();
		
		StringBuilder builder = new StringBuilder();
		for(char c='0';c<='9';++c){
			builder.append(c);
		}
//		for(char c='a';c<='z';++c){
//			builder.append(c);
//		}
		this.symbols = builder.toString().toCharArray();
	}
	
	public String generateCode(){
		StringBuilder builder = new StringBuilder();
		int l = symbols.length;
        for(int i=0;i<this.length;i++){
        	builder.append(symbols[random.nextInt(symbols.length)]);
        }
        return builder.toString();		
	}
	
	public static String getCode(int length){
		RandomString randomString = new RandomString(length);
		return randomString.generateCode();
	}
	
	
}
