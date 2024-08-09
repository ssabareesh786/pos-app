package com.increff.posapp.dto;

import com.increff.posapp.dao.BrandDao;
import com.increff.posapp.dao.InventoryDao;
import com.increff.posapp.dao.ProductDao;
import com.increff.posapp.model.InventoryReportData;
import com.increff.posapp.pojo.BrandPojo;
import com.increff.posapp.pojo.InventoryPojo;
import com.increff.posapp.pojo.ProductPojo;
import com.increff.posapp.service.AbstractUnitTest;
import com.increff.posapp.service.ApiException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ReportsDtoInventoryReportTest extends AbstractUnitTest {

    @Autowired
    private ReportsDto reportsDto;
    @Autowired
    private BrandDao brandDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private InventoryDao inventoryDao;

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
                    p.setQuantity(12);
                    inventoryDao.insert(p);
                    t++;
                    k++;
                }
            }
        }
    }
    @Test
    public void testGetInventoryReportPageAndSizeNull() throws ApiException {
        createInventory();

        List<InventoryReportData> list = reportsDto.getInventoryReport("", "", null, null).getContent();
        assertEquals(8, list.size());
        Integer k = 1;

        while(k <= 8){
            assertEquals("product"+k.toString(), list.get(k-1).getProductName());
            k++;
        }
    }

    @Test(expected = ApiException.class)
    public void testGetInventoryReportPageNull() throws ApiException {
        createInventory();
        reportsDto.getInventoryReport("", "", null, 9);
    }

    @Test(expected = ApiException.class)
    public void testGetInventoryReportSizeNull() throws ApiException {
        createInventory();
        reportsDto.getInventoryReport("", "", 0, null);
    }

    @Test
    public void testGetInventoryReportBrandNotEmpty() throws ApiException {
        createInventory();

        List<InventoryReportData> list = reportsDto.getInventoryReport("brand1", "", null, null).getContent();
        assertEquals(4, list.size());

        for(Integer k=1; k <= 4; k++){
            assertEquals("product"+k.toString(), list.get(k-1).getProductName());
        }

    }

    @Test
    public void testGetInventoryReportCategoryNotEmpty() throws ApiException {
        createInventory();

        List<InventoryReportData> list = reportsDto.getInventoryReport("", "category1", null, null).getContent();
        assertEquals(4, list.size());

        Integer[] integers = {1,2,5,6};
        int j = 0;
        for (Integer k: integers){
            assertEquals("product"+k.toString(), list.get(j++).getProductName());
        }
    }

    @Test
    public void testGetInventoryReportBrandAndCategory() throws ApiException {
        createInventory();

        List<InventoryReportData> list = reportsDto.getInventoryReport("brand1", "category1", null, null).getContent();
        assertEquals(2, list.size());

        for(Integer k=1; k <=2; k++) {
            assertEquals("brand1", list.get(k - 1).getBrand());
            assertEquals("category1", list.get(k - 1).getCategory());
            assertEquals("product" + k, list.get(k - 1).getProductName());
        }
    }

    @Test
    public void testGetInventoryReportByPage() throws ApiException {
        createInventory();

        Page<InventoryReportData> page = reportsDto.getInventoryReport("", "", 0, 2);
        List<InventoryReportData> list = page.getContent();

        assertEquals(2, list.size());
        assertEquals(8, page.getTotalElements());

        int k = 1;

        while(k <= 2){
            assertEquals("product"+ k, list.get(k-1).getProductName());
            k++;
        }
    }

    @Test
    public void testGetInventoryReportByPageInvalid() throws ApiException {
        createInventory();
        Page<InventoryReportData> data = reportsDto.getInventoryReport("", "", 100, 200);
        assertEquals(0L, data.getContent().size());
    }

    @Test
    public void testGetInventoryReportByPageBrand() throws ApiException {
        createInventory();
        Page<InventoryReportData> page = reportsDto.getInventoryReport("brand1", "", 0, 2);
        List<InventoryReportData> list = page.getContent();
        assertEquals(4, page.getTotalElements());
        assertEquals(2, list.size());

        for(Integer k=1; k <= 2; k++){
            assertEquals("product"+k.toString(), list.get(k-1).getProductName());
        }
    }

    @Test
    public void testGetInventoryReportByPageCategory() throws ApiException {
        createInventory();

        Page<InventoryReportData> page = reportsDto.getInventoryReport("", "category1", 1, 2);
        List<InventoryReportData> list = page.getContent();
        assertEquals(4, page.getTotalElements());
        assertEquals(2, list.size());

        for(Integer k=5; k <= 6; k++){
            assertEquals("product"+ k, list.get(k-5).getProductName());
        }
    }

    @Test
    public void testGetInventoryReportByPageBoth() throws ApiException {
        createInventory();

        Page<InventoryReportData> page = reportsDto.getInventoryReport("brand1", "category1", 0, 2);
        List<InventoryReportData> list = page.getContent();
        assertEquals(2, page.getTotalElements());
        assertEquals(2, list.size());

        for(Integer k=1; k <=2; k++) {
            assertEquals("brand1", list.get(k - 1).getBrand());
            assertEquals("category1", list.get(k - 1).getCategory());
            assertEquals("product" + k, list.get(k - 1).getProductName());
        }
    }

    @Test(expected = ApiException.class)
    public void testValidate() throws ApiException {
        createInventory();
        reportsDto.getInventoryReport("brand8", "category1", null, null);
    }
}
