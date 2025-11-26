package com.kopchak.hotel.dto;

import com.kopchak.hotel.domain.PhotoExtension;

public record RoomPhotoDTO(String name, PhotoExtension type, byte[] data) {
}