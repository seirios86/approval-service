package com.cardpaymentsystem.approvalservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cardpaymentsystem.approvalservice.entity.Approval;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {

}
