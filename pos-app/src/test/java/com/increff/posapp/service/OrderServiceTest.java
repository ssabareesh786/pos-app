package com.increff.posapp.service;

import com.increff.posapp.model.OrderStatus;
import com.increff.posapp.pojo.OrderPojo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrderServiceTest extends AbstractUnitTest {

    @Autowired
    private OrderService orderService;

    private OrderPojo addOrder() throws ApiException {
        OrderPojo p = new OrderPojo("Asia/Kolkata");
        return orderService.add(p);
    }
    @Test(expected = ApiException.class)
    public void testValidateNull() throws ApiException {
        OrderPojo p = new OrderPojo();
        p.setTime(null);
        orderService.add(p);
    }

    @Test
    public void testAdd() throws ApiException {
        OrderPojo p = new OrderPojo("Asia/Kolkata");
        orderService.add(p);
    }

    @Test
    public void testGetById() throws ApiException {
        OrderPojo p = addOrder();
        OrderPojo pojo = orderService.getById(p.getId());
        assertEquals(p.getId(), pojo.getId());
    }

    @Test(expected = ApiException.class)
    public void testGetByIdInvalid() throws ApiException {
        OrderPojo pojo = orderService.getById(1000002);
    }

    @Test(expected = ApiException.class)
    public void testValidateTimeIntervalLarge() throws ApiException {
        LocalDateTime startDateTime = LocalDateTime.of(2014, Month.JANUARY, 1, 10, 10,30);
        LocalDateTime endDateTime = LocalDateTime.of(2023, Month.FEBRUARY, 12, 0,10,10);
        ZonedDateTime startTime = ZonedDateTime.of(startDateTime, ZoneId.of("Asia/Kolkata"));
        ZonedDateTime endTime = ZonedDateTime.of(endDateTime, ZoneId.of("Asia/Kolkata"));
        orderService.getByInterval(startTime, endTime);
    }

    @Test(expected = ApiException.class)
    public void testValidateTimeIntervalInvalidStart() throws ApiException {
        LocalDateTime startDateTime = LocalDateTime.of(2014, Month.JANUARY, 1, 10, 10,30);
        LocalDateTime endDateTime = LocalDateTime.of(2023, Month.FEBRUARY, 16, 0,10,10);
        ZonedDateTime startTime = ZonedDateTime.of(startDateTime, ZoneId.of("Asia/Kolkata"));
        ZonedDateTime endTime = ZonedDateTime.of(endDateTime, ZoneId.of("Asia/Kolkata"));
        orderService.getByInterval(startTime, endTime);
    }

    @Test(expected = ApiException.class)
    public void testValidateTimeIntervalInvalidEnd() throws ApiException {
        LocalDateTime startDateTime = LocalDateTime.of(2023, Month.JANUARY, 1, 10, 10,30);
        LocalDateTime endDateTime = LocalDateTime.now().plusDays(10L);
        ZonedDateTime startTime = ZonedDateTime.of(startDateTime, ZoneId.of("Asia/Kolkata"));
        ZonedDateTime endTime = ZonedDateTime.of(endDateTime, ZoneId.of("Asia/Kolkata"));
        orderService.getByInterval(startTime, endTime);
    }
    @Test
    public void testGetByInterval() throws ApiException {
        LocalDateTime startDateTime = LocalDateTime.of(2023, Month.JANUARY, 1, 10, 10,30);
        LocalDateTime endDateTime = LocalDateTime.of(2023, Month.FEBRUARY, 11, 0,10,10);
        ZonedDateTime startTime = ZonedDateTime.of(startDateTime, ZoneId.of("Asia/Kolkata"));
        ZonedDateTime endTime = ZonedDateTime.of(endDateTime, ZoneId.of("Asia/Kolkata"));
        orderService.getByInterval(startTime, endTime);
    }

    @Test
    public void testGetAll(){
        orderService.getAll();
    }

    @Test
    public void testGetAllByPage() throws ApiException {
        addOrder();
        List<OrderPojo> orderPojoList = orderService.getAll(0, 5);
        assertTrue(orderPojoList.size() >0 && orderPojoList.size() <= 5);
    }

//    @Test
//    public void testUpdateById() throws ApiException {
//        OrderPojo p = new OrderPojo();
//        LocalDateTime localDateTime = LocalDateTime.of(2022, Month.MARCH, 2, 0, 10, 0);
//        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("Asia/Kolkata"));
//        p.setTime(zonedDateTime);
//        p.setOrderStatus(OrderStatus.NOT_INVOICED);
//        String localDate  = zonedDateTime.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
//        OrderPojo pojo1 = orderService.add(p);
//        assertEquals("02/03/2022", pojo1.getTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
//        OrderPojo pojo2 = new OrderPojo("Asia/Kolkata");
//        OrderPojo pojo3 = orderService.updateById(pojo1.getId(), pojo2);
//        assertEquals("16/03/2023", pojo3.getTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
//    }
}
