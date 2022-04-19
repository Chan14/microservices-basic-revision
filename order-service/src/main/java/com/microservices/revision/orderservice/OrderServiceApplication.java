package com.microservices.revision.orderservice;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.sleuth.instrument.async.TraceableExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableEurekaClient
@Slf4j
@EnableFeignClients
@RequiredArgsConstructor
public class OrderServiceApplication {

	private final BeanFactory beanFactory;

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

	@Bean
	public RequestInterceptor requestTokenBearerInterceptor() {
		return template -> {
			JwtAuthenticationToken jwtAuthenticationToken =
					(JwtAuthenticationToken) SecurityContextHolder
							.getContext()
							.getAuthentication();
			template.header("Authorization", "Bearer " + jwtAuthenticationToken.getToken().getTokenValue());
		};
	}
		/*return new RequestInterceptor() {
			@Override
			public void apply(RequestTemplate template) {
				JwtAuthenticationToken jwtAuthenticationToken =
						(JwtAuthenticationToken) SecurityContextHolder
								.getContext()
								.getAuthentication();
				template.header("Authorization", "Bearer " + jwtAuthenticationToken.getToken().getTokenValue());
			}
		};*/

	@Bean
	public ExecutorService traceableExecutorService() {
		ExecutorService executorService = Executors.newCachedThreadPool();
		return new TraceableExecutorService(beanFactory, executorService);
	}

}
