package com.increff.posapp.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InventoryData extends InventoryForm{

	private Integer productId;
	private String name;
}