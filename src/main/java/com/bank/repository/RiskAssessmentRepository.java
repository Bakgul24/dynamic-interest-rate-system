package com.bank.repository;

import com.bank.entity.RiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, Long> {

    @Query("SELECT r FROM RiskAssessment r WHERE r.customer.id = :customerId ORDER BY r.assessmentDate DESC LIMIT 1")
    Optional<RiskAssessment> findLatestByCustomerId(@Param("customerId") Long customerId);

    Optional<RiskAssessment> findByLoanId(Long loanId);

    List<RiskAssessment> findByRiskCategory(RiskAssessment.RiskCategory riskCategory);

    @Query("SELECT r FROM RiskAssessment r WHERE r.totalRiskScore >= :riskScore ORDER BY r.totalRiskScore DESC")
    List<RiskAssessment> findHighRiskAssessments(@Param("riskScore") Double riskScore);

    List<RiskAssessment> findByCustomerId(Long customerId);

    @Query("SELECT r FROM RiskAssessment r ORDER BY r.totalRiskScore DESC LIMIT :limit")
    List<RiskAssessment> findTopRiskyCustomers(@Param("limit") int limit);
}