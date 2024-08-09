package com.increff.posapp.controller;

import com.increff.posapp.dto.ReportsDto;
import com.increff.posapp.model.BrandData;
import com.increff.posapp.model.InventoryReportData;
import com.increff.posapp.model.SalesReportData;
import com.increff.posapp.model.SalesReportForm;
import com.increff.posapp.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Api
@RestController
@RequestMapping("/api-all/reports")
public class ReportsController {

    @Autowired
    private ReportsDto reportsDto;

    private static final Logger logger = Logger.getLogger(ReportsController.class);
    @ApiOperation(value = "Used to get the brand report")
    @RequestMapping(path = "/brand-report", method = RequestMethod.GET)
    public Page<BrandData> getBrandReport(
           @RequestParam(required = false) String brand,
           @RequestParam(required = false) String category,
            @RequestParam(name = "page-number", required = false) Integer page,
            @RequestParam(name = "page-size", required = false) Integer size
    ) throws ApiException {
        return reportsDto.getBrandReport(brand, category, page, size);
    }

    @ApiOperation(value = "Used to get the brand report")
    @RequestMapping(path = "/inventory-report", method = RequestMethod.GET)
    public Page<InventoryReportData> getInventoryReport(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String category,
            @RequestParam(name = "page-number") Integer page,
            @RequestParam(name = "page-size") Integer size
    ) throws ApiException {
        return reportsDto.getInventoryReport(brand, category, page, size);
    }

    @ApiOperation(value = "Used to get the brand report")
    @RequestMapping(path = "/sales-report", method = RequestMethod.POST)
    public SalesReportData getSalesReport(
            @RequestBody SalesReportForm salesReportForm,
            @RequestParam(name = "page-number") Integer page,
            @RequestParam(name = "page-size") Integer size
            ) throws ApiException {
            return reportsDto.getSalesReport(salesReportForm, page, size);
    }
}
