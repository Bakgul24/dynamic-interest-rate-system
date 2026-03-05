package com.bank.repository;

import com.bank.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByCustomerId(Long customerId);

    List<Loan> findByCustomerIdAndStatus(Long customerId, Loan.LoanStatus status);

    Optional<Loan> findByIdAndCustomerId(Long loanId, Long customerId);

    List<Loan> findByStatus(Loan.LoanStatus status);

    @Query("SELECT l FROM Loan l WHERE l.status =:status AND l.remainingMonths > 0")
    List<Loan> findActiveLoans(@Param("status") Loan.LoanStatus status);

    @Query("SELECT l FROM Loan l WHERE l.currentRiskScore >=:riskScore AND l.status = 'ACTIVE'")
    List<Loan> findHighRiskLoans(@Param("riskScore") Double riskScore);
}