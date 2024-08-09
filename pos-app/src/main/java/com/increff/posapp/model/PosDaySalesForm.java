package com.increff.posapp.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PosDaySalesForm {

	private LocalDateTime startDate;
	private LocalDateTime endDate;
}