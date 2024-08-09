package com.increff.posapp.dto;

import com.increff.posapp.dao.*;
import com.increff.posapp.model.OrderStatus;
import com.increff.posapp.model.PosDaySalesForm;
import com.increff.posapp.pojo.*;
import com.increff.posapp.service.AbstractUnitTest;
import com.increff.posapp.service.ApiException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronSequenceGenerator;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.assertNotNull;

public class PosDaySalesDtoTest extends AbstractUnitTest {

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
    private PosDaySalesDto posDaySalesDto;

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
        for(int i=1; i<=2; i++){
            for(int j=1; j<=2; j++){
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

    private List<Integer> createOrdersYesterday(Integer s, Integer e){
        createInventory();
        List<Integer> list = new ArrayList<>();
        for(int i=1; i<=2; i++) {
            OrderPojo orderPojo = new OrderPojo("Asia/Kolkata");
            orderPojo.setTime(ZonedDateTime.of(LocalDateTime.now().minusDays(1L),
                    ZoneId.of("Asia/Kolkata")));
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

    private List<Integer> createOrdersYesterdayAnother(Integer s, Integer e){
        List<Integer> list = new ArrayList<>();
        for(int i=1; i<=2; i++) {
            OrderPojo orderPojo = new OrderPojo("Asia/Kolkata");
            orderPojo.setTime(ZonedDateTime.of(LocalDateTime.now()
                    .plusDays(1L), ZoneId.of("Asia/Kolkata")));
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
        for(Integer orderId: orderIds){
            OrderPojo pojo = orderDao.selectById(orderId);
            pojo.setOrderStatus(OrderStatus.INVOICED);
            orderDao.update(pojo);
        }
    }
    @Test
    public void testUpdateScheduler() throws ApiException, InterruptedException {
        createOrdersYesterday(1,2);
        Thread.sleep(1000);
        posDaySalesDto.updatePosDaySalesTable();
        createOrdersYesterdayAnother(1, 2);
        CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator("0 0 0 * * ?");
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        Date nextExecutionTime = cronSequenceGenerator.next(new Date());
        long delay = nextExecutionTime.getTime() - System.currentTimeMillis();
        scheduledExecutorService.schedule(() ->{
                    try{
                        posDaySalesDto.updatePosDaySalesTable();
                    }
                    catch (ApiException ex){
                        //
                    }
        }, delay, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testUpdateSchedulerInvoiced() throws ApiException, InterruptedException {
        invoiceOrders(createOrdersYesterday(1,2));
        Thread.sleep(1000);
        posDaySalesDto.updatePosDaySalesTable();
        invoiceOrders(createOrdersYesterdayAnother(1, 2));
        CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator("0 0 0 * * ?");
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        Date nextExecutionTime = cronSequenceGenerator.next(new Date());
        long delay = nextExecutionTime.getTime() - System.currentTimeMillis();
        scheduledExecutorService.schedule(() ->{
            try{
                posDaySalesDto.updatePosDaySalesTable();
            }
            catch (ApiException ex){
                //
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testGetData() throws ApiException, InterruptedException, IllegalAccessException {
        invoiceOrders(createOrdersYesterday(1,2));
        Thread.sleep(1000);
        posDaySalesDto.updatePosDaySalesTable();
        invoiceOrders(createOrdersYesterdayAnother(1,2));
        Thread.sleep(1200);
        posDaySalesDto.updatePosDaySalesTable();
        PosDaySalesForm form = new PosDaySalesForm();
        form.setStartDate(LocalDateTime.now().minusDays(2L));
        form.setEndDate(LocalDateTime.now());
        assertNotNull(posDaySalesDto.getData(form, 0, 5));
    }

    @Test
    public void testGetDataPageAndSizeNull() throws ApiException, InterruptedException, IllegalAccessException {
        invoiceOrders(createOrdersYesterday(1,2));
        Thread.sleep(1000);
        posDaySalesDto.updatePosDaySalesTable();
        invoiceOrders(createOrdersYesterdayAnother(1,2));
        Thread.sleep(1200);
        posDaySalesDto.updatePosDaySalesTable();
        PosDaySalesForm form = new PosDaySalesForm();
        form.setStartDate(LocalDateTime.now().minusDays(2L));
        form.setEndDate(LocalDateTime.now());
        assertNotNull(posDaySalesDto.getData(form, null, null));
    }

    @Test(expected = ApiException.class)
    public void testGetDataInvalidStartDate() throws ApiException, IllegalAccessException {
        PosDaySalesForm form = new PosDaySalesForm();
        form.setStartDate(LocalDateTime.now().minusYears(3L));
        form.setEndDate(LocalDateTime.MAX);
        posDaySalesDto.getData(form, 0, 5);
    }

    @Test(expected = ApiException.class)
    public void testGetDataInvalidEndDate() throws ApiException, IllegalAccessException {
        PosDaySalesForm form = new PosDaySalesForm();
        form.setStartDate(LocalDateTime.now().minusDays(10L));
        form.setEndDate(LocalDateTime.now().plusDays(2L));
        posDaySalesDto.getData(form, 0, 5);
    }

    @Test(expected = ApiException.class)
    public void testGetDataPageNull() throws ApiException, IllegalAccessException {
        PosDaySalesForm form = new PosDaySalesForm();
        form.setStartDate(LocalDateTime.now().minusDays(10L));
        form.setEndDate(LocalDateTime.now());
        posDaySalesDto.getData(form, null, 5);
    }

    @Test(expected = ApiException.class)
    public void testGetDataSizeNull() throws ApiException, IllegalAccessException {
        PosDaySalesForm form = new PosDaySalesForm();
        form.setStartDate(LocalDateTime.now().minusDays(10L));
        form.setEndDate(LocalDateTime.now());
        posDaySalesDto.getData(form, 0, null);
    }
}
