package com.cardpaymentsystem.approvalservice.service.impl;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.YearMonth;

import org.springframework.stereotype.Service;

import com.cardpaymentsystem.approvalservice.controller.dto.ApprovalRequestDto;
import com.cardpaymentsystem.approvalservice.controller.dto.ApprovalResponseDto;
import com.cardpaymentsystem.approvalservice.entity.Approval;
import com.cardpaymentsystem.approvalservice.exception.ValidationException;
import com.cardpaymentsystem.approvalservice.repository.ApprovalRepository;
import com.cardpaymentsystem.approvalservice.service.ApiService;
import com.cardpaymentsystem.approvalservice.service.ApprovalService;
import com.cardpaymentsystem.approvalservice.util.EncryptionUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {

	private final ApiService apiService;
	private final ApprovalRepository approvalRepository;
	private final EncryptionUtil encryptionUtil;
	private static final String BLUE_WALNUT = "bluewalnut";
	private static final int ENCRYPTED_CARD_NUMBER = 0;
	private static final int EXPIRATION_YEAR_MONTH = 1;
	private static final int BASE_YEAR = 2000;

	public ApprovalResponseDto approvePayment(ApprovalRequestDto approvalRequestDto) throws Exception {

		validateApprovePayment(approvalRequestDto);

		String token = approvalRequestDto.getToken();
		String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);

		if (!apiService.verifyToken(encodedToken).isValid()) {
			throw new ValidationException("Invalid token");
		}

		String[] cardInfo = encryptionUtil.decrypt(token, approvalRequestDto.getTokenIv()).split("\\|");
		Approval approval = registerApproval(approvalRequestDto, cardInfo);

		return ApprovalResponseDto.builder()
			.approvalId(approvePayment(approval, cardInfo))
			.build();
	}

	private void validateApprovePayment(ApprovalRequestDto approvalRequestDto) throws Exception {

		String token = approvalRequestDto.getToken();
		if (token == null || token.isBlank()) {
			throw new ValidationException("No token");
		}
		String tokenIv = approvalRequestDto.getTokenIv();
		if (tokenIv == null || tokenIv.isBlank()) {
			throw new ValidationException("No IV for token");
		}
		String userCi = approvalRequestDto.getUserCi();
		if (userCi == null || userCi.isBlank()) {
			throw new ValidationException("No user CI");
		}
		BigDecimal paymentAmount = approvalRequestDto.getPaymentAmount();
		if (paymentAmount == null || paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new ValidationException("No payment amount");
		}
		String storeId = approvalRequestDto.getStoreId();
		if (storeId == null || storeId.isBlank()) {
			throw new ValidationException("No store ID");
		}
	}

	private Approval registerApproval(ApprovalRequestDto approvalRequestDto, String[] cardInfo) throws Exception {

		Approval approval = Approval.builder()
			.encryptedCardNumber(cardInfo[ENCRYPTED_CARD_NUMBER])
			.expirationYearMonth(cardInfo[EXPIRATION_YEAR_MONTH])
			.encryptedUserCi(encryptionUtil.encrypt(approvalRequestDto.getUserCi()))
			.storeId(approvalRequestDto.getStoreId())
			.paymentAmount(approvalRequestDto.getPaymentAmount())
			.pgCode(BLUE_WALNUT)
			.isApproved(false)
			.build();

		return approvalRepository.save(approval);
	}

	private Long approvePayment(Approval approval, String[] cardInfo) throws Exception {

		int month = Integer.parseInt(cardInfo[EXPIRATION_YEAR_MONTH].substring(0, 2));
		int year = Integer.parseInt(cardInfo[EXPIRATION_YEAR_MONTH].substring(2));
		if (YearMonth.of(year + BASE_YEAR, month).isBefore(YearMonth.now())) {
			throw new ValidationException("The card has expired");
		}

		approval.setApproved(true);
		approval.setApprovedDate(LocalDateTime.now());
		return approvalRepository.save(approval).getApprovalId();
	}
}
