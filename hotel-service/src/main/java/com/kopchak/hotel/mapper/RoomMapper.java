package com.kopchak.hotel.mapper;

import com.kopchak.hotel.domain.Room;
import com.kopchak.hotel.domain.RoomPhoto;
import com.kopchak.hotel.dto.CreateUpdateRoomDTO;
import com.kopchak.hotel.dto.RoomDTO;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    Room toRoom(CreateUpdateRoomDTO createRoomDto, Set<RoomPhoto> photos);
    RoomDTO toRoomDto (Room room);
    Set<RoomDTO> toSearchRoomDtoSet (List<Room> rooms);
}
