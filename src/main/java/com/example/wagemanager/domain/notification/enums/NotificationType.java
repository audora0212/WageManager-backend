package com.example.wagemanager.domain.notification.enums;

public enum NotificationType {
    SCHEDULE_CHANGE,        // 일정 변경
    CORRECTION_REQUEST,     // 정정 요청
    CORRECTION_RESPONSE,    // 정정 요청 응답
    PAYMENT_DUE,           // 급여 지급 예정
    PAYMENT_SUCCESS,       // 급여 입금 완료
    PAYMENT_FAILED         // 급여 송금 실패
}
