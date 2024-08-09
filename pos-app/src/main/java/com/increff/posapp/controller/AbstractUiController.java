package com.increff.posapp.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import com.increff.posapp.model.InfoData;
import com.increff.posapp.util.SecurityUtil;
import com.increff.posapp.util.UserPrincipal;

@Controller
public abstract class AbstractUiController {

	@Autowired
	private InfoData info;

	@Value("${app.baseUrl}")
	private String baseUrl;
	private static Logger logger = Logger.getLogger(AbstractUiController.class);

	protected ModelAndView mav(String page) {
		// Get current user
		UserPrincipal principal = SecurityUtil.getPrincipal();
		logger.info(principal);
		info.setEmail(principal == null ? "" : principal.getEmail());
		info.setRole(principal == null ? "" : principal.getRole());
		logger.info("Info: "+info);
		// Set info
		ModelAndView mav = new ModelAndView(page);
		mav.addObject("info", info);
		mav.addObject("baseUrl", baseUrl);
		return mav;
	}
	protected ModelAndView mav(String page, Integer orderId, String mode) {
		// Get current user
		UserPrincipal principal = SecurityUtil.getPrincipal();

		info.setEmail(principal == null ? "" : principal.getEmail());

		// Set info
		ModelAndView mav = new ModelAndView(page);
		mav.addObject("info", info);
		mav.addObject("baseUrl", baseUrl);
		mav.addObject("orderId", orderId);
		mav.addObject("mode", mode);
		return mav;
	}
}
