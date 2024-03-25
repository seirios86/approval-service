package com.cardpaymentsystem.approvalservice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Approval {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long approvalId;
	private String encryptedCardNumber;
	private String expirationYearMonth;
	private String encryptedUserCi;
	private String storeId;
	private BigDecimal paymentAmount;
	private String pgCode;
	private boolean isApproved;
	private LocalDateTime approvedDate;
	private LocalDateTime createdDate;
	private LocalDateTime updatedDate;

	@PrePersist
	public void prePersist() {

		this.createdDate = LocalDateTime.now();
		this.updatedDate = LocalDateTime.now();
	}

	@PreUpdate
	public void preUpdate() {

		this.updatedDate = LocalDateTime.now();
	}

	public void approvePayment() {

		this.isApproved = true;
		this.approvedDate = LocalDateTime.now();
	}
}
