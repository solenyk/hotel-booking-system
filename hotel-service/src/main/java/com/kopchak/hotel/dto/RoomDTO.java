package com.kopchak.hotel.dto;

import com.kopchak.hotel.domain.RoomReservationStatus;
import com.kopchak.hotel.domain.RoomType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record RoomDTO(Integer number, RoomType type, BigDecimal pricePerNight, RoomReservationStatus status,
                      Integer capacity, LocalDateTime createdAt, Set<RoomPhotoDTO> photos) {
}
