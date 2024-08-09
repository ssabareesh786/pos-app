package com.increff.posapp.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@Setter
public class SalesReportForm {

	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private String brand;
	private String category;
}