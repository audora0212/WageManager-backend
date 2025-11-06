package com.example.wagemanager.domain.contract.dto;

import com.example.wagemanager.domain.contract.entity.WorkerContract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ContractDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String workerCode;
        private BigDecimal hourlyWage;
        private String workDays; // JSON: [1,2,3,4,5,6,7]
        private LocalDate contractStartDate;
        private LocalDate contractEndDate;
        private Integer paymentDay;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private BigDecimal hourlyWage;
        private String workDays;
        private LocalDate contractEndDate;
        private Integer paymentDay;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long workplaceId;
        private String workplaceName;
        private Long workerId;
        private String workerName;
        private String workerCode;
        private String workerPhone;
        private BigDecimal hourlyWage;
        private String workDays;
        private LocalDate contractStartDate;
        private LocalDate contractEndDate;
        private Integer paymentDay;
        private Boolean isActive;

        public static Response from(WorkerContract contract) {
            return Response.builder()
                    .id(contract.getId())
                    .workplaceId(contract.getWorkplace().getId())
                    .workplaceName(contract.getWorkplace().getName())
                    .workerId(contract.getWorker().getId())
                    .workerName(contract.getWorker().getUser().getName())
                    .workerCode(contract.getWorker().getWorkerCode())
                    .workerPhone(contract.getWorker().getUser().getPhone())
                    .hourlyWage(contract.getHourlyWage())
                    .workDays(contract.getWorkDays())
                    .contractStartDate(contract.getContractStartDate())
                    .contractEndDate(contract.getContractEndDate())
                    .paymentDay(contract.getPaymentDay())
                    .isActive(contract.getIsActive())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListResponse {
        private Long id;
        private String workerName;
        private String workerCode;
        private String workerPhone;
        private BigDecimal hourlyWage;
        private LocalDate contractStartDate;
        private LocalDate contractEndDate;
        private Boolean isActive;

        public static ListResponse from(WorkerContract contract) {
            return ListResponse.builder()
                    .id(contract.getId())
                    .workerName(contract.getWorker().getUser().getName())
                    .workerCode(contract.getWorker().getWorkerCode())
                    .workerPhone(contract.getWorker().getUser().getPhone())
                    .hourlyWage(contract.getHourlyWage())
                    .contractStartDate(contract.getContractStartDate())
                    .contractEndDate(contract.getContractEndDate())
                    .isActive(contract.getIsActive())
                    .build();
        }
    }
}
