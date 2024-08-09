package com.increff.posapp.dto;

import com.increff.posapp.model.PosDaySalesData;
import com.increff.posapp.model.PosDaySalesForm;
import com.increff.posapp.pojo.OrderPojo;
import com.increff.posapp.pojo.PosDaySalesPojo;
import com.increff.posapp.service.ApiException;
import com.increff.posapp.service.OrderItemService;
import com.increff.posapp.service.OrderService;
import com.increff.posapp.service.PosDaySalesService;
import com.increff.posapp.util.Converter;
import com.increff.posapp.util.DoubleUtil;
import com.increff.posapp.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Component
public class PosDaySalesDto {
	@Autowired
	private PosDaySalesService posDaySalesService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderItemService orderItemService;

	@Scheduled(cron = "0 0 0 * * ?")
	public void updatePosDaySalesTable() throws ApiException {
		LocalDateTime startDateTime = LocalDateTime.now()
				.minusDays(1L).withHour(0).withMinute(0).withSecond(0);
		ZoneId zoneId = ZoneId.of("Asia/Kolkata");
		ZonedDateTime startDateTimeZoned = ZonedDateTime.of(startDateTime, zoneId);

		LocalDateTime endDateTime = LocalDateTime.now()
				.minusDays(1L).withHour(23).withMinute(59).withSecond(59);
		ZonedDateTime endDateTimeZoned = ZonedDateTime.of(endDateTime, zoneId);

		List<OrderPojo> orderPojoList = orderService.getByInterval(startDateTimeZoned, endDateTimeZoned);

		PosDaySalesPojo pojo = new PosDaySalesPojo();
		pojo.setDate(startDateTimeZoned);
		pojo.setInvoicedOrdersCount(orderPojoList.size());
		pojo.setInvoicedItemsCount(0);
		pojo.setTotalRevenue(0.0);
		for(OrderPojo p: orderPojoList){
			pojo.setInvoicedItemsCount((int) (pojo.getInvoicedItemsCount() + orderItemService.getTotalInvoicedQuantity(p.getId())));
			pojo.setTotalRevenue(pojo.getTotalRevenue() + orderItemService.getTotalCost(p.getId()));
		}
		pojo.setTotalRevenue(DoubleUtil.round(pojo.getTotalRevenue(), 2));
		posDaySalesService.add(pojo);
	}

	public Page<PosDaySalesData> getData(PosDaySalesForm form, Integer page, Integer size) throws ApiException {
		Validator.validate(form);
		validate(form);
		ZoneId zoneId = ZoneId.of("Asia/Kolkata");
		ZonedDateTime zonedDateTimeStart = ZonedDateTime.of(form.getStartDate(), zoneId);
		ZonedDateTime zonedDateTimeEnd = ZonedDateTime.of(form.getEndDate(), zoneId);

		if(page == null & size == null){
			List<PosDaySalesPojo> pojos = posDaySalesService.getByInterval(zonedDateTimeStart,
					zonedDateTimeEnd);
			if(pojos.size() == 0){
				throw new ApiException("Nothing to show");
			}
			List<PosDaySalesData> list = Converter.convertToPosDaySalesDataList(pojos);
			return new PageImpl<>(list, PageRequest.of(0, list.size()), list.size());
		}
		if (page != null && size != null){
			List<PosDaySalesPojo> pojos = posDaySalesService.getByInterval(zonedDateTimeStart,
					zonedDateTimeEnd, page, size);
			if(pojos.size() == 0){
				throw new ApiException("Nothing to show");
			}
			List<PosDaySalesData> list = Converter.convertToPosDaySalesDataList(pojos);
			return new PageImpl<>(list, PageRequest.of(page, size), posDaySalesService.getByIntervalTotalElements(zonedDateTimeStart, zonedDateTimeEnd));
		}

		throw new ApiException("Invalid request");
	}

	private void validate(PosDaySalesForm form) throws ApiException {
		if(form.getStartDate().toLocalDate().isBefore(LocalDate.now().minusYears(2L))){
			throw new ApiException("Enter a date within 2 years");
		}
		if(form.getEndDate().toLocalDate().isAfter(LocalDate.now())){
			throw new ApiException("End date should be today's date or a date before today");
		}
	}
}
