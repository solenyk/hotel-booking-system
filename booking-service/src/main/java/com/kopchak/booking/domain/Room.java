package com.kopchak.booking.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rooms")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class Room {

    @Id
    private Integer id;
}
