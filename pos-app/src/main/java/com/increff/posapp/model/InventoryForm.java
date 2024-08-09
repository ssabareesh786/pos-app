package com.increff.posapp.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InventoryForm {

	private String barcode;
	private Integer quantity;

}