package com.cardpaymentsystem.approvalservice.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.cardpaymentsystem.approvalservice.controller.dto.TokenValidationDto;
import com.cardpaymentsystem.approvalservice.exception.ApiCallException;
import com.cardpaymentsystem.approvalservice.service.ApiService;
import com.cardpaymentsystem.approvalservice.service.DiscoveryService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiServiceImpl implements ApiService {

	private final WebClient.Builder webClientBuilder;
	private final DiscoveryService discoveryService;
	private final ObjectMapper objectMapper;
	private static final String TOKEN_VERIFICATION_ERROR_PREFIX = "Token verification failed: ";
	private static final String FAILED_TO_RECEIVE_RESPONSE = "Failed to receive a response.";

	public TokenValidationDto verifyToken(String token) throws Exception {

		ResponseEntity<String> response;
		try {
			response = webClientBuilder.baseUrl(discoveryService.getApiGatewayUrl()).build()
				.get()
				.uri(uriBuilder -> uriBuilder
					.path("/api/v1/token/verify")
					.queryParam("token", token)
					.build())
				.retrieve()
				.toEntity(String.class)
				.block();

		} catch (WebClientResponseException e) {
			ProblemDetail problemDetail = objectMapper.readValue(e.getResponseBodyAsString(), ProblemDetail.class);
			throw new ApiCallException(TOKEN_VERIFICATION_ERROR_PREFIX + problemDetail.getDetail(),
				problemDetail.getStatus());
		}

		if (response == null) {
			throw new ApiCallException(TOKEN_VERIFICATION_ERROR_PREFIX + FAILED_TO_RECEIVE_RESPONSE,
				HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return objectMapper.readValue(response.getBody(), TokenValidationDto.class);
	}
}
