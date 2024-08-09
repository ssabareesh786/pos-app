package com.increff.posapp.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryReportData extends InventoryData{
    private String productName;
    private String brand;
    private String category;

}
