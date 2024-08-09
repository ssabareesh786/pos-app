package com.increff.posapp.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class UserForm {
	private String email;
	private String password;
}
