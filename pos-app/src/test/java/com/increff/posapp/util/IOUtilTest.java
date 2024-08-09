package com.increff.posapp.util;

import com.increff.posapp.service.AbstractUnitTest;
import org.junit.Test;

import java.io.*;

public class IOUtilTest extends AbstractUnitTest {

    @Test
    public void testIOCloseNull(){
        IOUtil.closeQuietly(null);
    }

    @Test
    public void testIOClose() throws IOException {
        File file = new File("test.txt");
        file.createNewFile();
        InputStream is = new FileInputStream(file);
        IOUtil.closeQuietly(is);
        file.deleteOnExit();
    }

    @Test
    public void testIOCloseException() throws IOException {
        File file = new File("test.txt");
        file.createNewFile();
        InputStream is = new FileInputStream(file);
        is.close();
        IOUtil.closeQuietly(is);
        file.deleteOnExit();
    }
}
