package org.microservice.service;

import org.microservice.common.Payment;
import org.microservice.common.TransactionRequest;
import org.microservice.common.TransactionResponse;
import org.microservice.model.Order;
import org.microservice.repository.OrderRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Logger;
import lombok.Value;

public class OrderService {

	Logger logger = LoggerFactory.getLogger(OrderService.class);
	@Autowired
	private OrderRepository repository;
	@Autowired
	@Lazy
	private RestTemplate template;

	@Value("${microservice.payment-service.endpoints.endpoint.uri}")
	private String ENDPOINT_URL;

	public TransactionResponse saveOrder(TransactionRequest request) throws JsonProcessingException {
		String response = "";
		Order order = request.getOrder();
		Payment payment = request.getPayment();
		payment.setOrderId(order.getId());
		payment.setAmount(order.getPrice());
		// rest call
		logger.info("Order-Service Request : " + new ObjectMapper().writeValueAsString(request));
		Payment paymentResponse = template.postForObject(ENDPOINT_URL, payment, Payment.class);

		response = paymentResponse.getPaymentStatus().equals("success")
				? "payment processing successful and order placed"
				: "there is a failure in payment api , order added to cart";
		logger.info("Order Service getting Response from Payment-Service : "
				+ new ObjectMapper().writeValueAsString(response));
		repository.save(order);
		return new TransactionResponse(order, paymentResponse.getAmount(), paymentResponse.getTransactionId(),
				response);
	}

}
