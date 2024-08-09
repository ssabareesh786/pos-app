package com.increff.posapp.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrderData {
	private Integer id;
	private String time;
	private String totalAmount;
	private OrderStatus orderStatus;
}
