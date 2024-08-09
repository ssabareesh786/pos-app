package com.increff.posapp.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class DoubleUtil {

	private DoubleUtil() {}
	public static Double round(Double value, Integer places){
		if (places < 0) throw new IllegalArgumentException();
		if(value == null)
			return null;
		else if(value.isNaN())
			return Double.NaN;
		else if(value == Double.POSITIVE_INFINITY)
			return Double.POSITIVE_INFINITY;
		else if(value ==Double.NEGATIVE_INFINITY)
			return Double.NEGATIVE_INFINITY;
		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static String roundToString(Double num) throws NumberFormatException{
		return String.format("%.2f", num);
	}
}
