package com.increff.posapp.util;

import com.increff.posapp.service.AbstractUnitTest;
import org.junit.Test;

public class DoubleUtilTest extends AbstractUnitTest {

    @Test(expected = IllegalArgumentException.class)
    public void testRoundNegativePlaces() throws IllegalArgumentException{
        DoubleUtil.round(12.34568, -1);
    }
}
