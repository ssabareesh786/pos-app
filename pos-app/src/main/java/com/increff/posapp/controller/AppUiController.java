package com.increff.posapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/ui")
public class AppUiController extends AbstractUiController {

	@RequestMapping(value = "/home")
	public ModelAndView home() {
		return mav("home.html");
	}

	@RequestMapping(value = "/brand")
	public ModelAndView brand() {
		return mav("brand.html");
	}

	@RequestMapping(value = "/admin")
	public ModelAndView admin() {
		return mav("user.html");
	}
	
	@RequestMapping(value = "/product")
	public ModelAndView product() {
		return mav("product.html");
	}
	
	@RequestMapping(value = "/inventory")
	public ModelAndView inventory() {
		return mav("inventory.html");
	}
	
	@RequestMapping(value = "/orders")
	public ModelAndView order() {
		return mav("orders.html");
	}
	
	@RequestMapping(value = "/order-items/{orderId}/{mode}")
	public ModelAndView orderItem(@PathVariable Integer orderId, @PathVariable String mode)
	{	
		return mav("order-items.html", orderId, mode);
	}

	@RequestMapping(value = "/order-placing")
	public ModelAndView orderPlacing() {
		return mav("order-placing.html");
	}

	@RequestMapping(value = "/brand-report")
	public ModelAndView brandReport(){ return mav("brand-report.html"); }

	@RequestMapping(value = "/inventory-report")
	public ModelAndView inventoryReport(){ return mav("inventory-report.html"); }

	@RequestMapping(value = "/sales-report")
	public ModelAndView salesReport(){ return mav("sales-report.html"); }

	@RequestMapping(value = "/daily-sales-report")
	public ModelAndView scheduler(){ return mav("daily-sales-report.html"); }
}
