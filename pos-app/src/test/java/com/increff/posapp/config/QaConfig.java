package com.increff.posapp.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;

import com.increff.posapp.spring.SpringConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@ComponentScan(//
		basePackages = { "com.increff.posapp" }, //
		excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = { SpringConfig.class })//
)
@PropertySources({ //
		@PropertySource(value = "classpath:./com/increff/posapp/test.properties", ignoreResourceNotFound = false) //
})
public class QaConfig {

}
