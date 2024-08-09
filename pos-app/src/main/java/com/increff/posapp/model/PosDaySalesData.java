package com.increff.posapp.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PosDaySalesData {
	private String date;
	private Integer invoicedOrdersCount;
	private Integer invoicedItemsCount;
	private Double totalRevenue;
}
