package com.cardpaymentsystem.approvalservice.service;

import com.cardpaymentsystem.approvalservice.controller.dto.TokenValidationDto;

public interface ApiService {

	TokenValidationDto verifyToken(String token) throws Exception;
}
