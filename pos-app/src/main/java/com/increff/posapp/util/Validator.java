package com.increff.posapp.util;

import com.increff.posapp.model.*;
import com.increff.posapp.service.ApiException;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

	private Validator() {}

	public static void validateOrderForm(OrderForm form) throws ApiException {
		if((form.getBarcodes().size() != form.getQuantities().size()) || (form.getBarcodes().size() != form.getSellingPrices().size())){
			throw new ApiException("Input form contains missing values");
		}
		Integer len = form.getBarcodes().size();
		for(Integer i=0; i < len; i++){
			validateBarcode(form.getBarcodes().get(i));
			validate("Quantity", form.getQuantities().get(i));
			validate("Selling price", form.getSellingPrices().get(i));
		}
	}

	public static void validateEmail(String s) throws ApiException {
		if(s == null){
			throw new ApiException("Email can't be empty");
		}
		if(s.length() > 30){
			throw new ApiException("Email length can't be greater tha 30");
		}
		String regex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+[.][a-zA-Z0-9.-]+$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(s);
		if(!matcher.matches()){
			throw new ApiException("Invalid email id entered as per email id standards");
		}
	}

	public static void validatePassword(String s) throws ApiException {
		if(s == null){
			throw new ApiException("Password can't be empty");
		}
		int count = 0;
		if(s.length() >= 8 && s.length() <= 32){
			if(Pattern.compile(".*\\\\d.*").matcher(s).matches()){
				count ++;
			}
			if(Pattern.compile(".*[a-z].*").matcher(s).matches()){
				count ++;
			}
			if(Pattern.compile(".*[A-Z].*").matcher(s).matches()){
				count ++;
			}
			if(!Pattern.compile("[A-Za-z0-9\\s]").matcher(s).matches()){
				count ++;
			}
		}

		if(count < 3){
			throw new ApiException("Password doesn't meet the standard requirements");
		}
	}

	public static void validateRole(String role) throws ApiException {
		if(!role.equals("supervisor") && !role.equals("operator")){
			throw new ApiException("Invalid role");
		}
	}

	public static void validateBarcode(String barcode) throws ApiException {
		if(barcode == null){
			throw new ApiException("Barcode is null");
		}
		String regex = "^[a-zA-Z0-9]*$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(barcode);
		if(!matcher.matches()){
			throw new ApiException("Barcode is invalid");
		}
	}

	public static <T> void validate(String field, T val) throws ApiException {
		if(val == null){
			throw new ApiException(field + " can't be empty");
		}
		if(val instanceof String){
			String value = (String) val;
			if(StringUtil.isEmpty(value)){
				throw new ApiException(field + " is empty");
			}
			if(StringUtil.isNotAlNum(value)){
				throw new ApiException(field + " should contain only alpha-numeric characters");
			}
			if(value.length() > 20){
				throw new ApiException("Number of characters in the field " + field + "can't be more than 20");
			}
		}
		else if(val instanceof Integer){
			if((Integer)val < 0){
				throw new ApiException(field + " can't be less than zero");
			}
		}
		else if(val instanceof Double){
			Double value = (Double) val;
			if(value < 0){
				throw new ApiException(field + " can't be less than zero");
			}
			if(value.isInfinite() || value.isNaN()){
				throw new ApiException(field + " is invalid");
			}
		}

	}

	public static void validate(Object o) throws ApiException {
		Field[] fields = o.getClass().getDeclaredFields();
		for(Field field: fields){
			field.setAccessible(true);
			try{
				if(field.get(o) == null){
					throw new ApiException(field.getName().substring(0,1).toUpperCase() +
							field.getName().substring(1) +
							" can't be empty");
				}
				if(field.getType().getSimpleName().equals("String")){
					String val = (String) field.get(o);
					if(StringUtil.isEmpty(val)){
						throw new ApiException(field.getName().substring(0,1).toUpperCase() +
								field.getName().substring(1) +
								" can't be empty");
					}
					if(StringUtil.isNotAlNum(val)){
						throw new ApiException(field.getName().substring(0,1).toUpperCase() +
								field.getName().substring(1) +
								" should contain only alpha-numeric characters");
					}
				}
				field.setAccessible(false);
			}
			catch (IllegalAccessException illegalAccessException){
				throw new ApiException("Unknown error occurred");
			}
		}
	}

}
