package com.increff.posapp.controller;

import com.increff.posapp.dto.PosDaySalesDto;
import com.increff.posapp.model.PosDaySalesData;
import com.increff.posapp.model.PosDaySalesForm;
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
public class PosDaySalesController {

	@Autowired
	private PosDaySalesDto posDaySalesDto;
	private static final Logger logger = Logger.getLogger(PosDaySalesController.class);

	@ApiOperation(value = "Gets the daily sales report between specified dates")
	@RequestMapping(path = "/daily-sales-report", method = RequestMethod.POST)
	public Page<PosDaySalesData> getDataByDate(
			@RequestBody PosDaySalesForm form,
			@RequestParam(name = "page-number") Integer page,
			@RequestParam(name = "page-size") Integer size) throws ApiException{
		return posDaySalesDto.getData(form, page, size);
	}
}
