package com.increff.posapp.service;

import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import org.junit.Test;

public class SampleTest {
	@Test
	public void testFiles() {
		InputStream is = null;
		is = SampleTest.class.getResourceAsStream("/com/increff/posapp/brand.tsv");
		assertNotNull(is);
	}

}
