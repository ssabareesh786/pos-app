package com.increff.posapp.controller;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.increff.posapp.model.MessageData;
import com.increff.posapp.service.ApiException;
@RestControllerAdvice
public class AppRestControllerAdvice {
	private Logger logger = Logger.getLogger(AppRestControllerAdvice.class);

	@ExceptionHandler(ApiException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public MessageData handle(ApiException e) {
		MessageData data = new MessageData();
		data.setMessage(e.getMessage());
		return data;
	}

	@ExceptionHandler(Throwable.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public MessageData handle(Throwable e) {
		MessageData data = new MessageData();
		data.setMessage("An unknown error has occurred" );
		logger.error(e.getMessage());
		return data;
	}
}