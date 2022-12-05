package ru.practicum.ewmservice.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "locations")
@Embeddable
public class Location {
    private Double lon;
    private Double lat;
}
