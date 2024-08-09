package com.increff.posapp.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SalesReportData{
	private String startDate;
	private String  endDate;
	private List<String> brands;
	private List<String> categories;
	private List<Integer> quantities;
	private List<String> totalAmounts;
	private String totalRevenue;
	private Long totalElements;

	public SalesReportData(){
		this.brands = new ArrayList<String>();
		this.categories = new ArrayList<String>();
		this.quantities = new ArrayList<Integer>();
		this.totalAmounts = new ArrayList<String>();
	}

}