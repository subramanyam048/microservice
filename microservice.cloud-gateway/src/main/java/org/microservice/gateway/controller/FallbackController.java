package org.microservice.gateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import reactor.core.publisher.Mono;

public class FallbackController {
	@RequestMapping("/orderFallBack")
	public Mono<String> orderServiceFallBack() {
		return Mono.just("Order Service is taking too long to respond or is down. Please try again later");
	}

	@RequestMapping("/paymentFallback")
	public Mono<String> paymentServiceFallBack() {
		return Mono.just("Payment Service is taking too long to respond or is down. Please try again later");
	}

}
