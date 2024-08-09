package com.increff.posapp.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserPrincipal {
	private int id;
	private String email;
	private String role;
}