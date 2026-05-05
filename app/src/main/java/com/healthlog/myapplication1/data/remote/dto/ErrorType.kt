package com.healthlog.myapplication1.data.remote.dto

enum class ErrorType {
    PARSE_FAILED,   // JSON 추출/파싱 실패 → 직접 입력 모드 안내
    NETWORK_ERROR,  // IOException → 오프라인 직접 입력
    API_LIMIT,      // HTTP 429 → 재시도 or 직접 입력
    UNKNOWN         // 그 외 서버 오류 (5xx 등)
}
