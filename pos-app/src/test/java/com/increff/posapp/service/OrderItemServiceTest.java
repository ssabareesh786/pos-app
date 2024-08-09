package com.increff.posapp.service;

import com.increff.posapp.dao.BrandDao;
import com.increff.posapp.dao.InventoryDao;
import com.increff.posapp.dao.OrderDao;
import com.increff.posapp.dao.ProductDao;
import com.increff.posapp.pojo.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrderItemServiceTest extends AbstractUnitTest {

    @Autowired
    private BrandDao brandDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private InventoryDao inventoryDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemService orderItemService;

    private BrandPojo addBrand(){
        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setBrand("brand1");
        brandPojo.setCategory("category1");
        return (BrandPojo) brandDao.insert(brandPojo);
    }
    private ProductPojo addProduct(){
        BrandPojo brandPojo = addBrand();
        ProductPojo productPojo = new ProductPojo();
        productPojo.setBarcode("asdfr56098");
        productPojo.setName("productname1");
        productPojo.setBrandCategory(brandPojo.getId());
        productPojo.setMrp(15.78);
        return (ProductPojo) productDao.insert(productPojo);
    }
    private InventoryPojo addInventory(){
        ProductPojo productPojo = addProduct();
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setProductId(productPojo.getId());
        inventoryPojo.setQuantity(12);
        return (InventoryPojo) inventoryDao.insert(inventoryPojo);
    }
    private OrderPojo addOrder() throws ApiException {
        OrderPojo p = new OrderPojo("Asia/Kolkata");
        return (OrderPojo) orderDao.insert(p);
    }
    private OrderItemPojo addOrderItem() throws ApiException {
        OrderPojo orderPojo = addOrder();
        OrderItemPojo p = new OrderItemPojo();
        p.setOrderId(orderPojo.getId());
        p.setProductId(addInventory().getProductId());
        p.setQuantity(6);
        p.setSellingPrice(12.78);
        return orderItemService.add(p);
    }

    @Test(expected = ApiException.class)
    public void testValidateOrderIdNull() throws ApiException {
        OrderItemPojo p = new OrderItemPojo();
        p.setOrderId(null);
        p.setProductId(234);
        p.setQuantity(1);
        p.setSellingPrice(12.345);
        orderItemService.add(p);
    }

    @Test(expected = ApiException.class)
    public void testValidateProductIdNull() throws ApiException {
        OrderItemPojo p = new OrderItemPojo();
        p.setOrderId(1);
        p.setProductId(null);
        p.setQuantity(1);
        p.setSellingPrice(12.345);
        orderItemService.add(p);
    }

    @Test(expected = ApiException.class)
    public void testValidateQuantityNull() throws ApiException {
        OrderItemPojo p = new OrderItemPojo();
        p.setOrderId(1);
        p.setProductId(12);
        p.setQuantity(null);
        p.setSellingPrice(12.345);
        orderItemService.add(p);
    }

    @Test(expected = ApiException.class)
    public void testValidateSellingPriceNull() throws ApiException {
        OrderItemPojo p = new OrderItemPojo();
        p.setOrderId(1);
        p.setProductId(1);
        p.setQuantity(1);
        p.setSellingPrice(null);
        orderItemService.add(p);
    }

    @Test(expected = ApiException.class)
    public void testValidateSellingPriceNegative() throws ApiException {
        OrderItemPojo p = new OrderItemPojo();
        p.setOrderId(1);
        p.setProductId(1);
        p.setQuantity(1);
        p.setSellingPrice(-12.345);
        orderItemService.add(p);
    }

    @Test(expected = ApiException.class)
    public void testValidateSellingPriceInfinite() throws ApiException {
        OrderItemPojo p = new OrderItemPojo();
        p.setOrderId(1);
        p.setProductId(1);
        p.setQuantity(1);
        p.setSellingPrice(Double.POSITIVE_INFINITY);
        orderItemService.add(p);
    }

    @Test(expected = ApiException.class)
    public void testValidateSellingPriceNan() throws ApiException {
        OrderItemPojo p = new OrderItemPojo();
        p.setOrderId(1);
        p.setProductId(1);
        p.setQuantity(1);
        p.setSellingPrice(Double.NaN);
        orderItemService.add(p);
    }


    @Test(expected = ApiException.class)
    public void testValidateQuantityInvalid() throws ApiException {
        OrderItemPojo p = new OrderItemPojo();
        p.setOrderId(1);
        p.setProductId(1);
        p.setQuantity(-9);
        p.setSellingPrice(12.345);
        orderItemService.add(p);
    }

    @Test
    public void testNormalize() throws ApiException {
        OrderItemPojo p = new OrderItemPojo();
        p.setOrderId(1);
        p.setProductId(12);
        p.setQuantity(1);
        p.setSellingPrice(12.345);
        assertEquals("12.35", orderItemService.add(p).getSellingPrice().toString());
    }

    @Test
    public void testGetById() throws ApiException {
        OrderItemPojo pojo1 = addOrderItem();
        orderItemService.getById(pojo1.getId());
    }

    @Test(expected = ApiException.class)
    public void testGetByIdInvalid() throws ApiException {
        orderItemService.getById(90003664);
    }

    @Test
    public void testGetByOrderId() throws ApiException {
        OrderItemPojo pojo1 = addOrderItem();
        orderItemService.getByOrderId(pojo1.getOrderId());
    }

    @Test(expected = ApiException.class)
    public void testGetByOrderIdInvalid() throws ApiException {
        orderItemService.getByOrderId(4576732);
    }

    @Test
    public void testGetPageByOrderId() throws ApiException {
        OrderItemPojo pojo1 = addOrderItem();
        List<OrderItemPojo> orderItemPojoList = orderItemService.getPageByOrderId(pojo1.getOrderId(), 0, 5);
        assertTrue(orderItemPojoList.size() > 0 && orderItemPojoList.size() <= 5 );
    }

    @Test
    public void testInvoiceQuantity() throws ApiException {
        Long total = orderItemService.getTotalInvoicedQuantity(10000678);
    }

    @Test
    public void testTotalCost(){
        Double total = orderItemService.getTotalCost(1000007654);
    }

    @Test
    public void testUpdate() throws ApiException {
        OrderItemPojo pojo1 = addOrderItem();
        OrderItemPojo pojo2 = new OrderItemPojo();
        pojo2.setOrderId(pojo1.getOrderId());
        pojo2.setProductId(pojo1.getProductId());
        pojo2.setQuantity(3);
        pojo2.setSellingPrice(13.78);
        OrderItemPojo pojo3 = orderItemService.updateById(pojo1.getId(), pojo2);
        assertEquals("13.78", pojo3.getSellingPrice().toString());
        assertEquals(3, (int) pojo3.getQuantity());
    }
}
