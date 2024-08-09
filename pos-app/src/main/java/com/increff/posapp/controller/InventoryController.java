package com.increff.posapp.controller;

import com.increff.posapp.dto.InventoryDto;
import com.increff.posapp.model.InventoryData;
import com.increff.posapp.model.InventoryForm;
import com.increff.posapp.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Api
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
	
	@Autowired
	private InventoryDto inventoryDto;

	@ApiOperation(value = "Adds a product to inventory if not exists or updates if exists")
	@RequestMapping(path = "", method = RequestMethod.POST)
	public InventoryData add(@RequestBody InventoryForm form) throws ApiException{
		return inventoryDto.add(form);
	}
	@ApiOperation(value = "Gets an item in the inventory by id")
	@RequestMapping(path = "/{productId}", method = RequestMethod.GET)
	public InventoryData get(@PathVariable Integer productId) throws ApiException {
		return inventoryDto.get(productId);
	}
	@ApiOperation(value = "Gets the requested inventory data")
	@RequestMapping(path = "", method = RequestMethod.GET)
	public Page<InventoryData> get(
			@RequestParam(name = "page-number", required = false) Integer page,
			@RequestParam(name = "page-size",required = false) Integer size
	) throws ApiException {
		return inventoryDto.get(page,size);
	}

	@ApiOperation(value = "Updates the inventory")
	@RequestMapping(path = "/{productId}", method = RequestMethod.PUT)
	public InventoryData update(
			@PathVariable Integer productId,
			@RequestBody InventoryForm f) throws ApiException{
		return inventoryDto.update(productId, f);
	}

}
