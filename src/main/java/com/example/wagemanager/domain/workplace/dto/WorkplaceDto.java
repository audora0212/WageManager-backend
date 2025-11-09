package com.example.wagemanager.domain.workplace.dto;

import com.example.wagemanager.domain.workplace.entity.Workplace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class WorkplaceDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String businessNumber;
        private String businessName;
        private String name;
        private String address;
        private String colorCode;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String businessName;
        private String name;
        private String address;
        private String colorCode;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String businessNumber;
        private String businessName;
        private String name;
        private String address;
        private String colorCode;
        private Boolean isActive;

        public static Response from(Workplace workplace) {
            return Response.builder()
                    .id(workplace.getId())
                    .businessNumber(workplace.getBusinessNumber())
                    .businessName(workplace.getBusinessName())
                    .name(workplace.getName())
                    .address(workplace.getAddress())
                    .colorCode(workplace.getColorCode())
                    .isActive(workplace.getIsActive())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListResponse {
        private Long id;
        private String name;
        private String businessName;
        private Integer workerCount;

        public static ListResponse from(Workplace workplace, Integer workerCount) {
            return ListResponse.builder()
                    .id(workplace.getId())
                    .name(workplace.getName())
                    .businessName(workplace.getBusinessName())
                    .workerCount(workerCount)
                    .build();
        }
    }
}
