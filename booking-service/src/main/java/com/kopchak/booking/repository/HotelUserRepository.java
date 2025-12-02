package com.kopchak.booking.repository;

import com.kopchak.booking.domain.HotelUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelUserRepository extends JpaRepository<HotelUser, Long> {
}
