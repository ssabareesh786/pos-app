package com.increff.posapp.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceData {
	
	private Integer id;
	private String time;

	private String totalAmount;
	
	@Override
	public String toString() {
		return "OrderData [id=" + id + ", time=" + time + "]";
	}
	
}
