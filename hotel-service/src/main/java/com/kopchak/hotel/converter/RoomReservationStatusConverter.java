package com.kopchak.hotel.converter;

import com.kopchak.hotel.domain.RoomReservationStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RoomReservationStatusConverter implements Converter<String, RoomReservationStatus> {

    @Override
    public RoomReservationStatus convert(String source) {
        return RoomReservationStatus.valueOf(source.toUpperCase());
    }
}
