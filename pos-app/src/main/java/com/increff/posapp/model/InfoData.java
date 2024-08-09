package com.increff.posapp.model;

import java.io.Serializable;

import lombok.ToString;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ToString
@Component
@Scope(value="session", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class InfoData implements Serializable {

	private static final long serialVersionUID = 1L;

	private String message;
	private String email;
	private String role;
	private String pageType;

	public InfoData() {
		message = "";
		email = "no email";
		role = "";
		pageType = "";
	}
}
