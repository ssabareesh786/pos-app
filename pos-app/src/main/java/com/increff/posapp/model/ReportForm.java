package com.increff.posapp.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ReportForm {
    private String brand;
    private String category;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
