package com.increff.posapp.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
public class ProductForm {

	private String barcode;
	private String brand;
	private String category;
	private String name;
	private Double mrp;
}
