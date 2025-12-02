package com.kopchak.hotel.dto;

import java.util.Map;

public record ValidationExceptionDto(Map<String, String> fieldsErrorDetails) {
}
