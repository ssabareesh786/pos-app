package com.increff.posapp.controller;

import com.increff.posapp.dto.BrandDto;
import com.increff.posapp.model.BrandData;
import com.increff.posapp.model.BrandForm;
import com.increff.posapp.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api
@RestController
@RequestMapping("/api/brands")
public class BrandController {
	private static final Logger logger = Logger.getLogger(BrandController.class);
	@Autowired
	private BrandDto brandDto;

	@ApiOperation(value = "Adds a new brand and category")
	@RequestMapping(path = "", method = RequestMethod.POST)
	public BrandData add(@RequestBody BrandForm form) throws ApiException{
		return brandDto.add(form);
	}

	@ApiOperation(value = "Gets brand and category by id")
	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
	public BrandData get(@PathVariable Integer id) throws ApiException, IllegalAccessException {
		return brandDto.get(id);
	}

	@ApiOperation(value = "Gets brand and category in a particular page")
	@RequestMapping(path = "", method = RequestMethod.GET)
	public Page<BrandData> get(
			@RequestParam(name = "page-number", required = false) Integer page,
			@RequestParam(name = "page-size", required = false) Integer size
	) throws ApiException {
		return brandDto.get(page, size);
	}

	@ApiOperation(value = "Updates a brand and category")
	@RequestMapping(path = "/{id}", method = RequestMethod.PUT)
	public BrandData update(@PathVariable int id, @Valid @RequestBody BrandForm form) throws ApiException {
		return brandDto.update(id, form);
	}

}