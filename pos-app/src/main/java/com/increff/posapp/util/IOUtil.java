package com.increff.posapp.util;

import java.io.Closeable;
import java.io.IOException;

public class IOUtil {

	private IOUtil() {}
	public static void closeQuietly(Closeable c) {
		if (c == null) {
			return;
		}

		try {
			c.close();
		} catch (IOException e) {
			// do nothing
		}
	}

}
