package com.cardpaymentsystem.approvalservice.service;

import com.cardpaymentsystem.approvalservice.controller.dto.ApprovalRequestDto;
import com.cardpaymentsystem.approvalservice.controller.dto.ApprovalResponseDto;

public interface ApprovalService {

	ApprovalResponseDto approvePayment(ApprovalRequestDto approvalRequestDto) throws Exception;
}
