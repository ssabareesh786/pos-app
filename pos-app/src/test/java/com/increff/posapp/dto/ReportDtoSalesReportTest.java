package com.increff.posapp.dto;

import com.increff.posapp.dao.*;
import com.increff.posapp.model.*;
import com.increff.posapp.pojo.*;
import com.increff.posapp.service.AbstractUnitTest;
import com.increff.posapp.service.ApiException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ReportDtoSalesReportTest extends AbstractUnitTest {

    @Autowired
    private BrandDao brandDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private InventoryDao inventoryDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private ReportsDto reportsDto;

    private BrandPojo addBrand(Integer b, Integer c){
        BrandPojo p = new BrandPojo();
        p.setBrand("brand"+b.toString());
        p.setCategory("category"+c.toString());
        return (BrandPojo) brandDao.insert(p);
    }

    private ProductPojo addProduct(Integer brandCategory, Integer p){
        ProductPojo productPojo = new ProductPojo();
        productPojo.setBarcode("barcode"+p.toString());
        productPojo.setName("product"+p.toString());
        productPojo.setBrandCategory(brandCategory);
        productPojo.setMrp(100.00 + p.doubleValue());
        return (ProductPojo) productDao.insert(productPojo);
    }

    private void createInventory(){
        int k = 1;
        for(int i=1; i<=3; i++){
            for(int j=1; j<=3; j++){
                BrandPojo brandPojo = addBrand(i,j);
                Integer brandCategory = brandPojo.getId();
                int t = 0;
                while(t < 2){
                    ProductPojo pojo = addProduct(brandCategory,k);
                    InventoryPojo p = new InventoryPojo();
                    p.setProductId(pojo.getId());
                    p.setQuantity(12+t);
                    inventoryDao.insert(p);
                    t++;
                    k++;
                }
            }
        }
    }

    private List<Integer> createOrders(Integer s, Integer e){
        createInventory();
        List<Integer> list = new ArrayList<>();
        for(int i=1; i<=2; i++) {
            OrderPojo orderPojo = new OrderPojo("Asia/Kolkata");
            OrderPojo orderPojo1 = (OrderPojo) orderDao.insert(orderPojo);
            list.add(orderPojo1.getId());
            for (int j = s; j <= e; j++) {
                OrderItemPojo pojo = new OrderItemPojo();
                pojo.setOrderId(orderPojo1.getId());
                pojo.setProductId(productDao.selectByBarcode("barcode" + j).getId());
                pojo.setQuantity(2);
                pojo.setSellingPrice(98.01);
                orderItemDao.insert(pojo);
            }
        }
        return list;
    }

    private void invoiceOrders(List<Integer> orderIds){
        for(Integer id: orderIds){
            OrderPojo pojo = orderDao.selectById(id);
            pojo.setOrderStatus(OrderStatus.INVOICED);
            orderDao.update(pojo);
        }
    }

    @Test
    public void testGetSalesReport() throws ApiException {
        List<Integer> orderIds = createOrders(1,2);
        invoiceOrders(orderIds);
        SalesReportForm form = new SalesReportForm();
        form.setStartDate(LocalDateTime.now().minusDays(10L));
        form.setEndDate(LocalDateTime.now());
        form.setBrand("");
        form.setCategory("");
        SalesReportData data = reportsDto.getSalesReport(form, 0, 5);

        assertEquals(
                LocalDateTime
                        .now()
                        .minusDays(10L)
                        .toLocalDate()
                        .format(
                                DateTimeFormatter
                                        .ofPattern("dd/MM/yyyy")),
                data.getStartDate()
        );
        assertEquals(LocalDate
                .now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), data.getEndDate());
//        assertEquals("784.08", data.getTotalRevenue());
    }

    @Test
    public void testGetDataBrand() throws ApiException {
        List<Integer> orderIds = createOrders(1,2);
        invoiceOrders(orderIds);
        SalesReportForm form = new SalesReportForm();
        form.setStartDate(LocalDateTime.now().minusDays(10L));
        form.setEndDate(LocalDateTime.now());
        form.setBrand("brand1");
        form.setCategory("");
        SalesReportData data = reportsDto.getSalesReport(form,0, 5);
        assertEquals(
                LocalDateTime
                        .now()
                        .minusDays(10L)
                        .toLocalDate()
                        .format(
                                DateTimeFormatter
                                        .ofPattern("dd/MM/yyyy")),
                data.getStartDate()
        );
        assertEquals(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), data.getEndDate());
//        assertEquals("784.08", data.getTotalRevenue());
    }

    @Test
    public void testGetDataBrandInvalid() throws ApiException {
        List<Integer> orderIds = createOrders(1,2);
        invoiceOrders(orderIds);
        SalesReportForm form = new SalesReportForm();
        form.setStartDate(LocalDateTime.now().minusDays(10L));
        form.setEndDate(LocalDateTime.now());
        form.setBrand("brand3");
        form.setCategory("");
        SalesReportData data = reportsDto.getSalesReport(form, 0, 5);
        assertEquals(
                LocalDateTime
                        .now()
                        .minusDays(10L)
                        .toLocalDate()
                        .format(
                                DateTimeFormatter
                                        .ofPattern("dd/MM/yyyy")),
                data.getStartDate()
        );
        assertEquals(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), data.getEndDate());
//        assertEquals("784.08", data.getTotalRevenue());
    }
    @Test
    public void testGetDataCategory() throws ApiException {
        List<Integer> orderIds = createOrders(1,2);
        invoiceOrders(orderIds);
        SalesReportForm form = new SalesReportForm();
        form.setStartDate(LocalDateTime.now().minusDays(10L));
        form.setEndDate(LocalDateTime.now());
        form.setBrand("");
        form.setCategory("category1");
        SalesReportData data = reportsDto.getSalesReport(form, 0, 5);
        assertEquals(
                LocalDateTime
                        .now()
                        .minusDays(10L)
                        .toLocalDate()
                        .format(
                                DateTimeFormatter
                                        .ofPattern("dd/MM/yyyy")),
                data.getStartDate()
        );
    }

    @Test
    public void testGetDataCategoryInvalid() throws ApiException {
        List<Integer> orderIds = createOrders(1,2);
        invoiceOrders(orderIds);
        SalesReportForm form = new SalesReportForm();
        form.setStartDate(LocalDateTime.now().minusDays(10L));
        form.setEndDate(LocalDateTime.now());
        form.setBrand("");
        form.setCategory("category3");
        SalesReportData data = reportsDto.getSalesReport(form, 0, 5);
        assertEquals(
                LocalDateTime
                        .now()
                        .minusDays(10L)
                        .toLocalDate()
                        .format(
                                DateTimeFormatter
                                        .ofPattern("dd/MM/yyyy")),
                data.getStartDate()
        );

    }

    @Test
    public void testGetDataBrandCategory() throws ApiException {
        List<Integer> orderIds = createOrders(1,2);
        invoiceOrders(orderIds);
        SalesReportForm form = new SalesReportForm();
        form.setStartDate(LocalDateTime.now().minusDays(10L));
        form.setEndDate(LocalDateTime.now());
        form.setBrand("brand1");
        form.setCategory("category1");
        SalesReportData data = reportsDto.getSalesReport(form, 0, 5);
        assertEquals(
                LocalDateTime
                        .now()
                        .minusDays(10L)
                        .toLocalDate()
                        .format(
                                DateTimeFormatter
                                        .ofPattern("dd/MM/yyyy")),
                data.getStartDate()
        );

    }

    @Test
    public void testGetDataBrandCategoryInvalid() throws ApiException {
        List<Integer> orderIds = createOrders(1,2);
        invoiceOrders(orderIds);
        SalesReportForm form = new SalesReportForm();
        form.setStartDate(LocalDateTime.now().minusDays(10L));
        form.setEndDate(LocalDateTime.now());
        form.setBrand("brand3");
        form.setCategory("category3");
        SalesReportData data = reportsDto.getSalesReport(form, 0, 5);
        assertEquals(
                LocalDateTime
                        .now()
                        .minusDays(10L)
                        .toLocalDate()
                        .format(
                                DateTimeFormatter
                                        .ofPattern("dd/MM/yyyy")),
                data.getStartDate()
        );
    }
    @Test(expected = ApiException.class)
    public void testGetDataStartDateInvalid() throws ApiException {
        List<Integer> orderIds = createOrders(1, 2);
        invoiceOrders(orderIds);
        SalesReportForm form = new SalesReportForm();
        form.setStartDate(LocalDateTime.now().minusYears(10L));
        form.setEndDate(LocalDateTime.now().plusMinutes(10L));
        form.setBrand("");
        form.setCategory("");
        reportsDto.getSalesReport(form, 0, 5);
    }
    @Test(expected = ApiException.class)
    public void testValidateEndDateInvalid() throws ApiException {
        SalesReportForm form = new SalesReportForm();
        form.setStartDate(LocalDateTime.now().minusDays(10L));
        form.setEndDate(LocalDateTime.now().plusDays(20L));
        form.setBrand("");
        form.setCategory("");
        reportsDto.getSalesReport(form, 0, 5);
    }

    @Test(expected = ApiException.class)
    public void testValidateDaysInvalid() throws ApiException {
        SalesReportForm form = new SalesReportForm();
        form.setStartDate(LocalDateTime.now().minusYears(2L));
        form.setEndDate(LocalDateTime.now());
        form.setBrand("");
        form.setCategory("");
        reportsDto.getSalesReport(form, 0, 5);
    }
}
