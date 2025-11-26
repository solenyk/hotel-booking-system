package com.kopchak.hotel.converter;

import com.kopchak.hotel.domain.RoomType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RoomTypeConverter implements Converter<String, RoomType> {

    @Override
    public RoomType convert(String source) {
        return RoomType.valueOf(source.toUpperCase());
    }
}
