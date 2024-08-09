package com.increff.posapp.service;

import com.increff.posapp.model.BrandData;
import com.increff.posapp.pojo.BrandPojo;
import com.increff.posapp.pojo.ProductPojo;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class ProductServiceTest extends AbstractUnitTest {

    @Autowired
    private BrandService brandService;
	@Autowired
	private ProductService productService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private BrandPojo addBrand() throws ApiException {
        BrandPojo p = new BrandPojo();
        p.setBrand("brand1");
        p.setCategory("category1");
        return brandService.add(p);
    }

    private BrandPojo addBrandAnother() throws ApiException {
        BrandPojo p = new BrandPojo();
        p.setBrand("brand2");
        p.setCategory("category2");
        return brandService.add(p);
    }
    private ProductPojo addProduct() throws ApiException {
        ProductPojo pojo = new ProductPojo();
        pojo.setBarcode("asdfgfhu");
        pojo.setName("product1");
        pojo.setBrandCategory(addBrand().getId());
        pojo.setMrp(339.765);
        return productService.add(pojo);
    }
    @Test(expected = ApiException.class)
    public void testValidateBarcodeNull() throws ApiException {
        ProductPojo p = new ProductPojo();
        p.setBarcode(null);
        p.setName("prodname1");
        p.setBrandCategory(addBrand().getId());
        p.setMrp(200.65);
        productService.add(p);
    }

    @Test(expected = ApiException.class)
    public void testValidateBarcodeEmpty() throws ApiException {
        ProductPojo p = new ProductPojo();
        p.setBarcode("");
        p.setName("prodname1");
        p.setBrandCategory(addBrand().getId());
        p.setMrp(200.65);
        productService.add(p);
    }

    @Test(expected = ApiException.class)
    public void testValidateBarcodeNotAlNum() throws ApiException {
        ProductPojo p = new ProductPojo();
        p.setBarcode("G6./&6gr1");
        p.setName("prodname1");
        p.setBrandCategory(addBrand().getId());
        p.setMrp(200.65);
        productService.add(p);
    }

    @Test(expected = ApiException.class)
    public void testValidateProductNameNull() throws ApiException {
        ProductPojo p = new ProductPojo();
        p.setBarcode("7hjyuakja");
        p.setName(null);
        p.setBrandCategory(addBrand().getId());
        p.setMrp(200.65);
        productService.add(p);
    }
    @Test(expected = ApiException.class)
    public void testValidateProductNameEmpty() throws ApiException {
        ProductPojo p = new ProductPojo();
        p.setBarcode("ghu6583");
        p.setName("");
        p.setBrandCategory(1);
        p.setMrp(200.65);
        productService.add(p);
    }

    @Test(expected = ApiException.class)
    public void testValidateProductNameNotAlNum() throws ApiException {
        ProductPojo p = new ProductPojo();
        p.setBarcode("ghu6583");
        p.setName("12$Nm/.,8*");
        p.setBrandCategory(1);
        p.setMrp(200.65);
        productService.add(p);
    }

    @Test(expected = ApiException.class)
    public void testValidateBrandCategoryNull() throws ApiException {
        ProductPojo p = new ProductPojo();
        p.setBarcode("oiuaif687");
        p.setName("prodname1");
        p.setBrandCategory(null);
        p.setMrp(200.65);
        productService.add(p);
    }
    @Test(expected = ApiException.class)
    public void testValidateMrpNull() throws ApiException {
        ProductPojo p = new ProductPojo();
        p.setBarcode("oiuaif687");
        p.setName("prodname1");
        p.setBrandCategory(addBrand().getId());
        p.setMrp(null);
        productService.add(p);
    }

    @Test(expected = ApiException.class)
    public void testValidateMrpPositiveInfinite() throws ApiException {
        ProductPojo p = new ProductPojo();
        p.setBarcode("oiuaif687");
        p.setName("prodname1");
        p.setBrandCategory(addBrand().getId());
        p.setMrp(Double.POSITIVE_INFINITY);
        productService.add(p);
    }
    @Test(expected = ApiException.class)
    public void testValidateMrpNegativeInfinite() throws ApiException {
        ProductPojo p = new ProductPojo();
        p.setBarcode("oiuaif687");
        p.setName("prodname1");
        p.setBrandCategory(addBrand().getId());
        p.setMrp(Double.NEGATIVE_INFINITY);
        productService.add(p);
    }
    @Test(expected = ApiException.class)
    public void testValidateMrpNan() throws ApiException {
        ProductPojo p = new ProductPojo();
        p.setBarcode("oiuaif687");
        p.setName("prodname1");
        p.setBrandCategory(addBrand().getId());
        p.setMrp(Double.NaN);
        productService.add(p);
    }

    @Test(expected = ApiException.class)
    public void testValidateMrpNegative() throws ApiException {
        ProductPojo p = new ProductPojo();
        p.setBarcode("oiuaif687");
        p.setName("prodname1");
        p.setBrandCategory(addBrand().getId());
        p.setMrp(-20.000);
        productService.add(p);
    }

    @Test
    public void testValidateDuplicateBarcode() throws ApiException {
        ProductPojo p = new ProductPojo();
        p.setBarcode("oiuaif687");
        p.setName("prodname1");
        p.setBrandCategory(addBrand().getId());
        p.setMrp(20.000);
        productService.add(p);
        ProductPojo pojo = new ProductPojo();
        pojo.setBarcode("oiuaif687");
        pojo.setName("productname1");
        pojo.setBrandCategory(addBrandAnother().getId());
        pojo.setMrp(20.87);
        thrown.expect(ApiException.class);
        thrown.expectMessage("The entered barcode already exists");
        productService.add(pojo);
    }

    @Test
    public void testNormalize() throws ApiException {
        ProductPojo p = new ProductPojo();
        p.setBarcode("   \r \n OiUaIf687");
        p.setName("  ProDnamE1  \r\t ");
        p.setBrandCategory(addBrand().getId());
        p.setMrp(20.123);
        productService.add(p);
        assertEquals("oiuaif687", p.getBarcode());
        assertEquals("prodname1", p.getName());
        assertEquals("20.12", p.getMrp().toString());
    }
    @Test(expected = ApiException.class)
    public void testNormalizeInvalidInput() throws ApiException {
        ProductPojo p = new ProductPojo();
        p.setBarcode("barcode1");
        p.setName("product1");
        p.setBrandCategory(addBrand().getId());
        p.setMrp(null);
        productService.add(p);
    }
    public void testAdd() throws ApiException {
        ProductPojo productPojo = new ProductPojo();
        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setBrand("BrandName1");
        brandPojo.setCategory("Category1");
        BrandPojo brandPojo1 = brandService.add(brandPojo);
        productPojo.setBarcode("asfgfhj");
        productPojo.setBrandCategory(brandPojo1.getId());
        productPojo.setName("Product1");
        productPojo.setMrp(667.654);
        ProductPojo pojo = productService.add(productPojo);
        assertEquals(brandPojo1.getId(), productPojo.getBrandCategory());
        assertEquals("asdfgfhj", pojo.getBarcode());
        assertEquals("product1", pojo.getName());
        assertEquals("667.65", pojo.getMrp().toString());
    }

    @Test
    public void testGetById() throws ApiException {
        ProductPojo pojo1 = addProduct();
        ProductPojo pojoReturned = productService.getById(pojo1.getId());
        assertEquals("asdfgfhu", pojoReturned.getBarcode());
        assertEquals("product1", pojoReturned.getName());
        assertEquals(pojo1.getBrandCategory(), pojoReturned.getBrandCategory());
        assertEquals("339.77", pojoReturned.getMrp().toString());
    }
    @Test(expected = ApiException.class)
    public void testGetByIdInvalid() throws ApiException {
        productService.getById(1000000001);

    }
    @Test
    public void testGetByBarcode() throws ApiException {
        ProductPojo pojo1 = addProduct();
        ProductPojo pojoReturned = productService.getByBarcode(pojo1.getBarcode());
        assertEquals("asdfgfhu", pojoReturned.getBarcode());
        assertEquals("product1", pojoReturned.getName());
        assertEquals(pojo1.getBrandCategory(), pojoReturned.getBrandCategory());
        assertEquals("339.77", pojoReturned.getMrp().toString());
    }
    @Test(expected = ApiException.class)
    public void testGetByBarcodeBarcode() throws ApiException {
        productService.getByBarcode("aaaa");
    }
    @Test
    public void testGetAll() throws ApiException {
        ProductPojo pojo1 = addProduct();
        List<ProductPojo> productPojoList = productService.getAll();
        assertTrue(productPojoList.size() > 0);
    }

    @Test
    public void testGetAllByPage() throws ApiException {
        ProductPojo pojo1 = addProduct();
        List<ProductPojo> productPojoList = productService.getAll(0,5);
        assertTrue(productPojoList.size() > 0 && productPojoList.size() <= 5);
    }

    @Test
    public void testUpdateById() throws ApiException {
        ProductPojo pojo1 = addProduct();
        ProductPojo pojo2 = new ProductPojo();
        pojo2.setBarcode("asdf434ngf4");
        pojo2.setName("product2");
        pojo2.setBrandCategory(pojo1.getBrandCategory());
        pojo2.setMrp(223.54);
        ProductPojo pojoReturned = productService.updateById(pojo1.getId(), pojo2);
        assertEquals("asdf434ngf4", pojoReturned.getBarcode());
        assertEquals("product2", pojoReturned.getName());
        assertEquals(pojo1.getBrandCategory(), pojoReturned.getBrandCategory());
        assertEquals("223.54", pojoReturned.getMrp().toString());
    }
}
