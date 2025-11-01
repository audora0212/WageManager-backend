package com.example.wagemanager.domain.payment.entity;

import com.example.wagemanager.common.BaseEntity;
import com.example.wagemanager.domain.payment.enums.PaymentMethod;
import com.example.wagemanager.domain.payment.enums.PaymentStatus;
import com.example.wagemanager.domain.salary.entity.Salary;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_id", nullable = false, unique = true)
    private Salary salary;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    public void complete(String transactionId) {
        this.status = PaymentStatus.COMPLETED;
        this.paymentDate = LocalDateTime.now();
        this.transactionId = transactionId;
    }

    public void fail(String failureReason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = failureReason;
    }
}
