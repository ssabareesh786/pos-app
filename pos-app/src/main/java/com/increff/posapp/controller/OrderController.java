package com.increff.posapp.controller;

import com.increff.posapp.dto.OrderDto;
import com.increff.posapp.model.OrderData;
import com.increff.posapp.model.OrderForm;
import com.increff.posapp.model.OrderItemData;
import com.increff.posapp.model.OrderItemEditForm;
import com.increff.posapp.pojo.OrderItemPojo;
import com.increff.posapp.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.fop.apps.FOPException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;

@Api
@RestController
public class OrderController {

	@Autowired
	private OrderDto orderDto;
	private static final Logger logger = Logger.getLogger(OrderController.class);

	@ApiOperation(value = "Adds an order")
	@RequestMapping(path = "/api/orders", method = RequestMethod.POST)
	public List<OrderItemData> add(@RequestBody OrderForm form) throws ApiException{
		return orderDto.add(form);
	}

	@ApiOperation(value = "Gets list of all ordered items by order id")
	@RequestMapping(path = "/api/orders/{orderId}", method = RequestMethod.GET)
	public List<OrderItemData> getByOrderId(@PathVariable Integer orderId) throws ApiException {
		return orderDto.getByOrderId(orderId);
	}

	@ApiOperation(value = "Gets list of all ordered items by order id")
	@RequestMapping(path = "/api/order-items", method = RequestMethod.GET)
	public Page<OrderItemData> getPageByOrderId(@RequestParam(name = "order-id") Integer orderId,
												@RequestParam(name = "page-number") Integer page,
												@RequestParam(name = "page-size") Integer size) throws ApiException {
		return orderDto.getPageByOrderId(orderId, page, size);
	}

	@ApiOperation(value = "Gets list of all orders")
	@RequestMapping(path = "/api/orders", method = RequestMethod.GET)
	public Page<OrderData> getAll(
			@RequestParam(name = "page-number") Integer page,
			@RequestParam(name = "page-size") Integer size) throws ApiException {
		return orderDto.getAll(page, size);
	}

	@ApiOperation(value = "Gets an order item by id")
	@RequestMapping(path = "/api/order-items/{id}", method = RequestMethod.GET)
	public OrderItemData getOrderItem(@PathVariable Integer id) throws ApiException {
		return orderDto.getByOrderItemId(id);
	}

	@ApiOperation(value = "Used to download invoice")
	@RequestMapping(path = "/api/invoice/download/{orderId}", method = RequestMethod.GET)
	public void convertToPdf(@PathVariable Integer orderId, HttpServletResponse response)
			throws ApiException{
		logger.info("OrderId = "+orderId);
		orderDto.convertToPdf(orderId, response);
		logger.info("Response character encoding: "+response.getCharacterEncoding());
		logger.info("Response content type: "+response.getContentType());
		logger.info("Response status: "+response.getStatus());
	}

	@ApiOperation(value = "Edits an order item by id")
	@RequestMapping(path = "/api/order-items/edit/{id}", method = RequestMethod.PUT)
	public OrderItemData updateExistingOrderItem(@PathVariable Integer id, @RequestBody OrderItemEditForm orderItemEditForm) throws ApiException {
		return orderDto.update(id, orderItemEditForm);
	}

	@ApiOperation(value = "Edits an order item by id")
	@RequestMapping(path = "/api/order-items/add/{orderId}", method = RequestMethod.PUT)
	public List<OrderItemData> addNewOrderItems(@PathVariable Integer orderId, @RequestBody OrderForm form) throws ApiException, IllegalAccessException {
		return orderDto.addNewItems(orderId, form);
	}

	@ApiOperation(value = "Edits an order item by id")
	@RequestMapping(path = "/api/order-items/{id}", method = RequestMethod.DELETE)
	public Integer deleteOrderItem(@PathVariable Integer id) throws ApiException{
		return orderDto.deleteOrderItem(id);
	}
}