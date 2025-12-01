package com.kopchak.booking.dto;

import java.util.Map;

public record ValidationExceptionDto(Map<String, String> fieldsErrorDetails) {
}
