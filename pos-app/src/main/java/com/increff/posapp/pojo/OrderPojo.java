package com.increff.posapp.pojo;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.persistence.*;

import com.increff.posapp.model.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "orders")
@Getter
@Setter
@ToString
public class OrderPojo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(nullable = false)
	private ZonedDateTime time;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "ENUM('INVOICED', 'NOT_INVOICED')",name = "order_status",nullable = false)
	private OrderStatus orderStatus;

	public OrderPojo(){}
	public OrderPojo(String zone){
		LocalDateTime localDateTime = LocalDateTime.now();
		ZoneId india = ZoneId.of(zone);
		this.time = ZonedDateTime.of(localDateTime, india);
		this.orderStatus = OrderStatus.NOT_INVOICED;
	}
}
