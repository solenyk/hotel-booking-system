package com.kopchak.hotel.service;

import com.kopchak.hotel.domain.Room;
import com.kopchak.hotel.domain.RoomPhoto;
import com.kopchak.hotel.domain.RoomType;
import com.kopchak.hotel.dto.CreateUpdateRoomDTO;
import com.kopchak.hotel.dto.RoomDTO;
import com.kopchak.hotel.mapper.RoomMapper;
import com.kopchak.hotel.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomPhotoCompressionService photoCompressionService;
    private final RoomMapper roomMapper;
    private final RoomRepository roomRepository;

    public void createRoom(CreateUpdateRoomDTO createRoomDto, List<MultipartFile> photos) {
        Set<RoomPhoto> roomPhotos = photoCompressionService.convertMultipartFilesToPhotos(photos);
        Room room = roomMapper.toRoom(createRoomDto, roomPhotos);
        roomPhotos.forEach(roomPhoto -> roomPhoto.setRoom(room));
        roomRepository.save(room);
    }

    public void updateRoom(Integer id, CreateUpdateRoomDTO createRoomDto, List<MultipartFile> photos) {
        Room existingRoom = roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found"));
        Set<RoomPhoto> roomPhotos = photoCompressionService.convertMultipartFilesToPhotos(photos);
        Room room = roomMapper.toRoom(createRoomDto, roomPhotos);
        room.setId(id);
        room.setCreatedAt(existingRoom.getCreatedAt());
        roomPhotos.forEach(roomPhoto -> roomPhoto.setRoom(room));
        roomRepository.save(room);
    }

    public Set<RoomDTO> searchRooms(Integer capacity, BigDecimal minPrice, BigDecimal maxPrice, RoomType type,
                                    Integer number) {
        Specification<Room> spec = (root, query, cb) -> cb.conjunction();
        if (capacity != null) {
            spec = spec.and(((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("capacity"), capacity)));
        }
        if (minPrice != null) {
            spec = spec.and(((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("pricePerNight"), minPrice)));
        }
        if (maxPrice != null) {
            spec = spec.and(((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("pricePerNight"), maxPrice)));
        }
        if (type != null) {
            spec = spec.and(((root, query, cb) ->
                    cb.equal(root.get("type"), type)));
        }
        if (number != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("number"), number));
        }
        List<Room> rooms = roomRepository.findAll(spec);
        return roomMapper.toSearchRoomDtoSet(rooms);
    }

    public RoomDTO getRoomById(Integer roomId) {
        Optional<Room> room = roomRepository.findById(roomId);
        if (room.isPresent()) {
            return roomMapper.toRoomDto(room.get());
        }
        throw new RuntimeException("Product with id " + roomId + " is not found");
    }

    public void deleteRoomById(Integer roomId) {
        roomRepository.deleteById(roomId);
    }
}
