package com.kopchak.hotel.controller;

import com.kopchak.hotel.domain.RoomType;
import com.kopchak.hotel.dto.CreateUpdateRoomDTO;
import com.kopchak.hotel.dto.RoomDTO;
import com.kopchak.hotel.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createRoom(@RequestPart("room") @Valid CreateUpdateRoomDTO createRoomDto,
                           @RequestPart(name = "photos", required = false) List<MultipartFile> photos) {
        roomService.createRoom(createRoomDto, photos);
    }

    @GetMapping
    public ResponseEntity<Set<RoomDTO>> searchRooms(@RequestParam(required = false) Integer capacity,
                                                    @RequestParam(required = false, value = "min-price") BigDecimal minPrice,
                                                    @RequestParam(required = false, value = "max-price") BigDecimal maxPrice,
                                                    @RequestParam(required = false) RoomType type,
                                                    @RequestParam(required = false) Integer number) {
        Set<RoomDTO> searchRooms = roomService.searchRooms(capacity, minPrice, maxPrice, type, number);
        return ResponseEntity.ok(searchRooms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDTO> getRoom(@PathVariable Integer id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateRoom(@PathVariable Integer id,
                           @RequestPart("room") @Valid CreateUpdateRoomDTO createRoomDto,
                           @RequestPart(name = "photos", required = false) List<MultipartFile> photos) {
        roomService.updateRoom(id, createRoomDto, photos);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoom(@PathVariable Integer id) {
        roomService.deleteRoomById(id);
    }
}
