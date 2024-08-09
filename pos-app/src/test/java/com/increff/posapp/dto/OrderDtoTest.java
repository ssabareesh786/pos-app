package com.increff.posapp.dto;

import com.increff.posapp.dao.*;
import com.increff.posapp.model.*;
import com.increff.posapp.pojo.*;
import com.increff.posapp.service.AbstractUnitTest;
import com.increff.posapp.service.ApiException;
import com.increff.posapp.util.Converter;
import org.apache.fop.apps.FOPException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderDtoTest extends AbstractUnitTest {

    @Autowired
    private OrderDto orderDto;
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
    private BrandPojo addBrand1(){
        BrandPojo pojo = new BrandPojo();
        pojo.setBrand("brand1");
        pojo.setCategory("category1");
        return (BrandPojo) brandDao.insert(pojo);
    }
    private BrandPojo addBrand2(){
        BrandPojo pojo = new BrandPojo();
        pojo.setBrand("brand2");
        pojo.setCategory("category2");
        return (BrandPojo) brandDao.insert(pojo);
    }
    private BrandPojo addBrand3(){
        BrandPojo pojo = new BrandPojo();
        pojo.setBrand("brand3");
        pojo.setCategory("category3");
        return (BrandPojo) brandDao.insert(pojo);
    }
    private ProductPojo addProduct1() throws ApiException {
        BrandPojo brandPojo = addBrand1();
        ProductPojo pojo = new ProductPojo();
        pojo.setBarcode("barcode123");
        pojo.setBrandCategory(brandPojo.getId());
        pojo.setName("product1");
        pojo.setMrp(123.45);
        return (ProductPojo) productDao.insert(pojo);
    }
    private ProductPojo addProduct2() throws ApiException {
        BrandPojo brandPojo = addBrand2();
        ProductPojo pojo = new ProductPojo();
        pojo.setBarcode("barcode1234");
        pojo.setBrandCategory(brandPojo.getId());
        pojo.setName("product2");
        pojo.setMrp(12.45);
        return (ProductPojo) productDao.insert(pojo);
    }
    private ProductPojo addProduct3() throws ApiException {
        BrandPojo brandPojo = addBrand3();
        ProductPojo pojo = new ProductPojo();
        pojo.setBarcode("barcode12345");
        pojo.setBrandCategory(brandPojo.getId());
        pojo.setName("product3");
        pojo.setMrp(1234.56);
        return (ProductPojo) productDao.insert(pojo);
    }

    private InventoryPojo addInventory1() throws ApiException {
        ProductPojo pojo = addProduct1();
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setProductId(pojo.getId());
        inventoryPojo.setQuantity(21);
        return (InventoryPojo) inventoryDao.insert(inventoryPojo);
    }

    private InventoryPojo addInventory2() throws ApiException {
        ProductPojo pojo = addProduct2();
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setProductId(pojo.getId());
        inventoryPojo.setQuantity(23);
        return (InventoryPojo) inventoryDao.insert(inventoryPojo);
    }
    private InventoryPojo addInventory3() throws ApiException {
        ProductPojo pojo = addProduct3();
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setProductId(pojo.getId());
        inventoryPojo.setQuantity(26);
        return (InventoryPojo) inventoryDao.insert(inventoryPojo);
    }

    private List<OrderItemPojo> addOrderItems() throws ApiException {

        List<OrderItemPojo> list = new ArrayList<>();

        InventoryPojo inventoryPojo1 = addInventory1();
        InventoryPojo inventoryPojo2 = addInventory2();
        OrderPojo pojo = new OrderPojo("Asia/Kolkata");
        OrderPojo orderPojo = (OrderPojo) orderDao.insert(pojo);
        OrderItemPojo orderItemPojo1 = new OrderItemPojo();
        orderItemPojo1.setOrderId(orderPojo.getId());
        orderItemPojo1.setProductId(inventoryPojo1.getProductId());
        orderItemPojo1.setQuantity(5);
        orderItemPojo1.setSellingPrice(120.68);
        list.add((OrderItemPojo) orderItemDao.insert(orderItemPojo1));

        OrderItemPojo orderItemPojo2 = new OrderItemPojo();
        orderItemPojo2.setOrderId(orderPojo.getId());
        orderItemPojo2.setProductId(inventoryPojo2.getProductId());
        orderItemPojo2.setQuantity(2);
        orderItemPojo2.setSellingPrice(10.78);
        list.add((OrderItemPojo) orderItemDao.insert(orderItemPojo2));

        return list;
    }

    private List<OrderItemData> addOrder() throws ApiException, IllegalAccessException {
        addInventory1();
        addInventory2();
        OrderForm form = new OrderForm();
        form.getBarcodes().add("barcode123");
        form.getQuantities().add(2);
        form.getSellingPrices().add(120.68);
        form.getBarcodes().add("barcode1234");
        form.getQuantities().add(2);
        form.getSellingPrices().add(10.78);
        return orderDto.add(form);
    }

    @Test
    public void testGetByOrderId() throws ApiException {
        List<OrderItemPojo> list1 = addOrderItems();
        Integer orderId = list1.get(0).getOrderId();
        List<OrderItemData> list2 = orderDto.getByOrderId(orderId);

        assertEquals(list1.size(), list2.size());
        for(int i=0; i < list1.size(); i++){
            assertEquals(list1.get(i).getOrderId(), list2.get(i).getOrderId());
            assertEquals(list1.get(i).getQuantity(), list2.get(i).getQuantity());
            assertEquals(list1.get(i).getSellingPrice().toString(), list2.get(i).getSellingPrice());
        }
        assertEquals("product1", list2.get(0).getProductName());
        assertEquals("product2", list2.get(1).getProductName());
        assertNotEquals(list1.get(0).getProductId(), list1.get(1).getProductId());
    }

    @Test
    public void testAdd() throws ApiException, IllegalAccessException {
        InventoryPojo inventoryPojo1 = addInventory1();
        InventoryPojo inventoryPojo2 = addInventory2();
        OrderForm form = new OrderForm();
        form.getBarcodes().add("barcode123");
        form.getQuantities().add(2);
        form.getSellingPrices().add(120.68);
        form.getBarcodes().add("barcode1234");
        form.getQuantities().add(2);
        form.getSellingPrices().add(10.78);
        List<OrderItemData> list = orderDto.add(form);
        assertEquals("barcode123", list.get(0).getBarcode());
        assertEquals("product1", list.get(0).getProductName());
        assertEquals(2, list.get(0).getQuantity().intValue());
        assertEquals("120.68", list.get(0).getSellingPrice());
        assertEquals("123.45", list.get(0).getMrp());

        assertEquals("barcode1234", list.get(1).getBarcode());
        assertEquals("product2", list.get(1).getProductName());
        assertEquals(2, list.get(1).getQuantity().intValue());
        assertEquals("10.78", list.get(1).getSellingPrice());
        assertEquals("12.45", list.get(1).getMrp());

        assertNotNull(list.get(0).getOrderId());
        assertNotNull(list.get(1).getOrderId());
        assertEquals(list.get(0).getOrderId(), list.get(1).getOrderId());
        assertEquals(19, inventoryPojo1.getQuantity().intValue());
        assertEquals(21, inventoryPojo2.getQuantity().intValue());
    }

    @Test
    public void testGetAll() throws ApiException, IllegalAccessException {
        List<OrderItemData> list = addOrder();
        List<OrderData> orderDataList = orderDto.getAll();
        assertEquals(2, list.size());
        assertEquals(1, orderDataList.size());
        assertEquals(
                LocalDate.now(
                        ZoneId.of("Asia/Kolkata")
                ).format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                ),
                orderDataList.get(0).getTime().substring(0, 10)
        );
        assertEquals("262.92",orderDataList.get(0).getTotalAmount());
    }

    @Test
    public void testGetAllPage() throws ApiException, IllegalAccessException {
        List<OrderItemData> list = addOrder();
        List<OrderData> orderDataList = orderDto.getAll(0, 5).getContent();
        assertEquals(2, list.size());
        assertEquals(1, orderDataList.size());
        assertTrue(orderDataList.size() > 0 && orderDataList.size() <= 5);
        assertEquals(
                LocalDate.now(
                        ZoneId.of("Asia/Kolkata")
                ).format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                ),
                orderDataList.get(0).getTime().substring(0, 10)
        );
        assertEquals("262.92",orderDataList.get(0).getTotalAmount());
    }

    @Test
    public void testConvertToPdf() throws ApiException, FOPException, JAXBException, IOException, TransformerException, IllegalAccessException {
        HttpServletResponse response = new MockHttpServletResponse();
        List<OrderItemData> list = addOrder();
        assertEquals(2,list.size());
        orderDto.convertToPdf(list.get(0).getOrderId(), response);
        assertEquals("application/pdf", response.getContentType());
        assertTrue(response.getBufferSize() > 0);
    }

    @Test(expected = ApiException.class)
    public void testValidateMrpInvalid() throws ApiException, IllegalAccessException {
        addInventory1();
        addInventory2();
        OrderForm form = new OrderForm();
        form.getBarcodes().add("barcode123");
        form.getQuantities().add(200);
        form.getSellingPrices().add(1200.78);
        form.getBarcodes().add("barcode1234");
        form.getQuantities().add(29);
        form.getSellingPrices().add(1008.7);
        orderDto.add(form);
    }

    @Test(expected = ApiException.class)
    public void testValidateQuantityInvalid() throws ApiException, IllegalAccessException {
        addInventory1();
        addInventory2();
        OrderForm form = new OrderForm();
        form.getBarcodes().add("barcode123");
        form.getQuantities().add(290);
        form.getSellingPrices().add(120.68);
        form.getBarcodes().add("barcode1234");
        form.getQuantities().add(287);
        form.getSellingPrices().add(10.78);
        orderDto.add(form);
    }
    @Test
    public void testGetPageByOrderId() throws ApiException {
        List<OrderItemPojo> list1 = addOrderItems();
        Integer orderId = list1.get(0).getOrderId();
        List<OrderItemData> list2 = orderDto.getPageByOrderId(orderId, 0, 5).getContent();
        assertEquals(list1.size(), list2.size());
        assertTrue(list2.size() > 0 && list2.size() <= 5);
        for(int i=0; i < list1.size(); i++){
            assertEquals(list1.get(i).getOrderId(), list2.get(i).getOrderId());
            assertEquals(list1.get(i).getQuantity(), list2.get(i).getQuantity());
            assertEquals(list1.get(i).getSellingPrice().toString(), list2.get(i).getSellingPrice());
        }
        assertEquals("product1", list2.get(0).getProductName());
        assertEquals("product2", list2.get(1).getProductName());
        assertNotEquals(list1.get(0).getProductId(), list1.get(1).getProductId());
    }

    @Test
    public void testGetByOrderItemId() throws ApiException {
        List<OrderItemPojo> list1 = addOrderItems();
        OrderItemData data = orderDto.getByOrderItemId(list1.get(0).getId());
        assertEquals(list1.get(0).getId(), data.getId());
        assertEquals(list1.get(0).getOrderId(), data.getOrderId());
        assertEquals("product1", data.getProductName());
        assertEquals(list1.get(0).getQuantity(), data.getQuantity());
        assertEquals(list1.get(0).getSellingPrice().toString(), data.getSellingPrice());
    }

    // Test edit orders
    @Test
    public void testUpdateOrder() throws ApiException, IllegalAccessException {
        List<OrderItemPojo> list1 = addOrderItems();
        OrderItemEditForm editForm = new OrderItemEditForm();
        editForm.setId(list1.get(0).getId());
        editForm.setBarcode("barcode123");
        editForm.setQuantity(7);
        editForm.setSellingPrice(110.89);
        OrderItemData pojo = orderDto.update(list1.get(0).getId(), editForm);
        assertEquals(list1.get(0).getId(), pojo.getId());
        assertEquals(list1.get(0).getOrderId(), pojo.getOrderId());
        assertEquals("product1", pojo.getProductName());
        assertEquals("7", pojo.getQuantity().toString());
        assertEquals("110.89", pojo.getSellingPrice().toString());
    }

    @Test(expected = ApiException.class)
    public void testUpdateOrderIdInvalid() throws ApiException{
        List<OrderItemPojo> list1 = addOrderItems();
        OrderItemEditForm editForm = new OrderItemEditForm();
        editForm.setId(list1.get(0).getId() + 12345);
        editForm.setBarcode("barcode123");
        editForm.setQuantity(7);
        editForm.setSellingPrice(110.89);
       orderDto.update(list1.get(0).getId() + 12345, editForm);
    }

    @Test(expected = ApiException.class)
    public void testUpdateOrderIdNull() throws ApiException{
        List<OrderItemPojo> list1 = addOrderItems();
        OrderItemEditForm editForm = new OrderItemEditForm();
        editForm.setId(null);
        editForm.setBarcode("barcode123");
        editForm.setQuantity(7);
        editForm.setSellingPrice(110.89);
        orderDto.update(null, editForm);
    }

    @Test(expected = ApiException.class)
    public void testUpdateOrderIdBarcodeNull() throws ApiException{
        List<OrderItemPojo> list1 = addOrderItems();
        OrderItemEditForm editForm = new OrderItemEditForm();
        editForm.setId(list1.get(0).getId());
        editForm.setBarcode(null);
        editForm.setQuantity(7);
        editForm.setSellingPrice(110.89);
        orderDto.update(list1.get(0).getId(), editForm);
    }

    @Test(expected = ApiException.class)
    public void testUpdateOrderIdBarcodeInvalid() throws ApiException{
        List<OrderItemPojo> list1 = addOrderItems();
        OrderItemEditForm editForm = new OrderItemEditForm();
        editForm.setId(list1.get(0).getId());
        editForm.setBarcode("barcode1../23");
        editForm.setQuantity(7);
        editForm.setSellingPrice(110.89);
        orderDto.update(list1.get(0).getId(), editForm);
    }

    @Test(expected = ApiException.class)
    public void testUpdateQuantityNull() throws ApiException{
        List<OrderItemPojo> list1 = addOrderItems();
        OrderItemEditForm editForm = new OrderItemEditForm();
        editForm.setId(list1.get(0).getId());
        editForm.setBarcode("barcode123");
        editForm.setQuantity(null);
        editForm.setSellingPrice(110.89);
        orderDto.update(list1.get(0).getId(), editForm);
    }
    @Test(expected = ApiException.class)
    public void testUpdateQuantityNegative() throws ApiException{
        List<OrderItemPojo> list1 = addOrderItems();
        OrderItemEditForm editForm = new OrderItemEditForm();
        editForm.setId(list1.get(0).getId());
        editForm.setBarcode("barcode123");
        editForm.setQuantity(-70);
        editForm.setSellingPrice(110.89);
        orderDto.update(list1.get(0).getId(), editForm);
    }

    @Test(expected = ApiException.class)
    public void testUpdateQuantityZero() throws ApiException{
        List<OrderItemPojo> list1 = addOrderItems();
        OrderItemEditForm editForm = new OrderItemEditForm();
        editForm.setId(list1.get(0).getId());
        editForm.setBarcode("barcode123");
        editForm.setQuantity(0);
        editForm.setSellingPrice(110.89);
        orderDto.update(list1.get(0).getId(), editForm);
    }

    @Test(expected = ApiException.class)
    public void testUpdateQuantityGreaterThanAvailable() throws ApiException{
        List<OrderItemPojo> list1 = addOrderItems();
        OrderItemEditForm editForm = new OrderItemEditForm();
        editForm.setId(list1.get(0).getId());
        editForm.setBarcode("barcode123");
        editForm.setQuantity(7000);
        editForm.setSellingPrice(110.89);
        orderDto.update(list1.get(0).getId(), editForm);
    }

    @Test
    public void testUpdateQuantityLessThanPrevious() throws ApiException{
        List<OrderItemPojo> list1 = addOrderItems();
        OrderItemEditForm editForm = new OrderItemEditForm();
        editForm.setId(list1.get(0).getId());
        editForm.setBarcode("barcode123");
        editForm.setQuantity(1);
        editForm.setSellingPrice(110.89);
        OrderItemData pojo = orderDto.update(list1.get(0).getId(), editForm);
        assertEquals(list1.get(0).getId(), pojo.getId());
        assertEquals(list1.get(0).getOrderId(), pojo.getOrderId());
        assertEquals("product1", pojo.getProductName());
        assertEquals("1", pojo.getQuantity().toString());
        assertEquals("110.89", pojo.getSellingPrice().toString());
    }

    @Test
    public void testUpdateQuantityGreaterThanPrevious() throws ApiException{
        List<OrderItemPojo> list1 = addOrderItems();
        OrderItemEditForm editForm = new OrderItemEditForm();
        editForm.setId(list1.get(0).getId());
        editForm.setBarcode("barcode123");
        editForm.setQuantity(7);
        editForm.setSellingPrice(110.89);
        OrderItemData pojo = orderDto.update(list1.get(0).getId(), editForm);
        assertEquals(list1.get(0).getId(), pojo.getId());
        assertEquals(list1.get(0).getOrderId(), pojo.getOrderId());
        assertEquals("product1", pojo.getProductName());
        assertEquals("7", pojo.getQuantity().toString());
        assertEquals("110.89", pojo.getSellingPrice().toString());
    }

    @Test(expected = ApiException.class)
    public void testUpdateSellingPriceNull() throws ApiException{
        List<OrderItemPojo> list1 = addOrderItems();
        OrderItemEditForm editForm = new OrderItemEditForm();
        editForm.setId(list1.get(0).getId());
        editForm.setBarcode("barcode123");
        editForm.setQuantity(1);
        editForm.setSellingPrice(null);
        orderDto.update(list1.get(0).getId(), editForm);
    }

    @Test(expected = ApiException.class)
    public void testUpdateSellingPriceGreaterThanMrp() throws ApiException{
        List<OrderItemPojo> list1 = addOrderItems();
        OrderItemEditForm editForm = new OrderItemEditForm();
        editForm.setId(list1.get(0).getId());
        editForm.setBarcode("barcode123");
        editForm.setQuantity(1);
        editForm.setSellingPrice(10000000.90);
        orderDto.update(list1.get(0).getId(), editForm);
    }

    @Test(expected = ApiException.class)
    public void testUpdateSellingPriceZero() throws ApiException{
        List<OrderItemPojo> list1 = addOrderItems();
        OrderItemEditForm editForm = new OrderItemEditForm();
        editForm.setId(list1.get(0).getId());
        editForm.setBarcode("barcode123");
        editForm.setQuantity(1);
        editForm.setSellingPrice(0.0);
        orderDto.update(list1.get(0).getId(), editForm);
    }

    @Test(expected = ApiException.class)
    public void testUpdateSellingPriceNegative() throws ApiException{
        List<OrderItemPojo> list1 = addOrderItems();
        OrderItemEditForm editForm = new OrderItemEditForm();
        editForm.setId(list1.get(0).getId());
        editForm.setBarcode("barcode123");
        editForm.setQuantity(1);
        editForm.setSellingPrice(-90.98);
        orderDto.update(list1.get(0).getId(), editForm);
    }

    @Test(expected = ApiException.class)
    public void testUpdateSellingPriceNan() throws ApiException{
        List<OrderItemPojo> list1 = addOrderItems();
        OrderItemEditForm editForm = new OrderItemEditForm();
        editForm.setId(list1.get(0).getId());
        editForm.setBarcode("barcode123");
        editForm.setQuantity(1);
        editForm.setSellingPrice(Double.NaN);
        orderDto.update(list1.get(0).getId(), editForm);
    }

    @Test(expected = ApiException.class)
    public void testUpdateSellingPricePositiveInfinity() throws ApiException{
        List<OrderItemPojo> list1 = addOrderItems();
        OrderItemEditForm editForm = new OrderItemEditForm();
        editForm.setId(list1.get(0).getId());
        editForm.setBarcode("barcode123");
        editForm.setQuantity(1);
        editForm.setSellingPrice(Double.POSITIVE_INFINITY);
        orderDto.update(list1.get(0).getId(), editForm);
    }

    @Test(expected = ApiException.class)
    public void testUpdateSellingPriceNegativeInfinity() throws ApiException{
        List<OrderItemPojo> list1 = addOrderItems();
        OrderItemEditForm editForm = new OrderItemEditForm();
        editForm.setId(list1.get(0).getId());
        editForm.setBarcode("barcode123");
        editForm.setQuantity(1);
        editForm.setSellingPrice(Double.NEGATIVE_INFINITY);
        orderDto.update(list1.get(0).getId(), editForm);
    }

    @Test(expected = ApiException.class)
    public void testUpdateInvoiced() throws ApiException{
        List<OrderItemPojo> list1 = addOrderItems();
        OrderPojo orderPojo = orderDao.selectById(list1.get(0).getOrderId());
        orderPojo.setOrderStatus(OrderStatus.INVOICED);
        orderDao.update(orderPojo);
        OrderItemEditForm editForm = new OrderItemEditForm();
        editForm.setId(list1.get(0).getId());
        editForm.setBarcode("barcode123");
        editForm.setQuantity(1);
        editForm.setSellingPrice(Double.NEGATIVE_INFINITY);
        orderDto.update(list1.get(0).getId(), editForm);
    }

    @Test
    public void testAddNewItems() throws ApiException{
        List<OrderItemPojo> list1 = addOrderItems();
        OrderForm form = new OrderForm();
        addInventory3();
        form.getBarcodes().add("barcode12345");
        form.getQuantities().add(2);
        form.getSellingPrices().add(12.56);
        List<OrderItemData> list = orderDto.addNewItems(list1.get(0).getOrderId(), form);
        assertTrue(list.size() > 0);
        assertEquals(list1.get(0).getOrderId(), list.get(0).getOrderId());
        assertEquals("2", list.get(0).getQuantity().toString());
        assertEquals("12.56", list.get(0).getSellingPrice().toString());
        assertEquals("product3", list.get(0).getProductName());
    }

    @Test
    public void testAddNewItemsAddExisting() throws ApiException{
        List<OrderItemPojo> list1 = addOrderItems();
        OrderForm form = new OrderForm();
        form.getBarcodes().add("barcode1234");
        form.getQuantities().add(2);
        form.getSellingPrices().add(10.78);
        List<OrderItemData> list = orderDto.addNewItems(list1.get(0).getOrderId(), form);
        assertTrue(list.size() > 0);
        assertEquals(list1.get(0).getOrderId(), list.get(0).getOrderId());
        assertEquals("4", list.get(0).getQuantity().toString());
        assertEquals("10.78", list.get(0).getSellingPrice().toString());
        assertEquals("product2", list.get(0).getProductName());
    }

    @Test
    public void testDeleteOrderItem() throws ApiException{
        List<OrderItemPojo> list1 = addOrderItems();
        orderDto.deleteOrderItem(list1.get(0).getId());
    }

    @Test(expected = ApiException.class)
    public void testDeleteOrderItemInvalidId() throws ApiException{
        List<OrderItemPojo> list1 = addOrderItems();
        orderDto.deleteOrderItem(list1.get(0).getId()+67890);
    }

}
