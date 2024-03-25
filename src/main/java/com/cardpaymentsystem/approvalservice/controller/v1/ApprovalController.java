package com.cardpaymentsystem.approvalservice.controller.v1;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cardpaymentsystem.approvalservice.controller.dto.ApprovalRequestDto;
import com.cardpaymentsystem.approvalservice.controller.dto.ApprovalResponseDto;
import com.cardpaymentsystem.approvalservice.service.ApprovalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Approval Service")
@RestController
@RequestMapping("/api/v1/approval")
@RequiredArgsConstructor
public class ApprovalController {

	private final ApprovalService approvalService;

	@Operation(summary = "Approve Payment")
	@Parameter(name = "token", required = true, description = "token")
	@Parameter(name = "tokenIv", required = true, description = "randomly generated IV for decrypt token")
	@Parameter(name = "userCi", required = true, description = "user CI")
	@Parameter(name = "paymentAmount", required = true, description = "payment amount")
	@Parameter(name = "storeId", required = true, description = "store ID")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "OK",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApprovalResponseDto.class))),
		@ApiResponse(responseCode = "400", description = "Bad Request",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
		@ApiResponse(responseCode = "500", description = "Internal Server Error",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
	})
	@PostMapping("")
	public ResponseEntity<?> approvePayment(@RequestBody ApprovalRequestDto approvalRequestDto) throws Exception {

		return ResponseEntity.status(HttpStatus.OK).body(approvalService.approvePayment(approvalRequestDto));
	}
}
