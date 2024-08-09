package com.increff.posapp.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class SignUpForm {

	private String email;
	private String password;
}
