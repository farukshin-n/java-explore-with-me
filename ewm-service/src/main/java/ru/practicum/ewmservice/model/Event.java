package ru.practicum.ewmservice.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;
    @Column
    private String title;
    @Column
    private String annotation;
    @Column
    private String description;
    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Column
    private boolean paid;
    @JoinColumn(name = "initiator_id", nullable = false)
    @ManyToOne(optional = false)
    private User initiator;
    @Embedded
    private Location location;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @Column(name = "created")
    private LocalDateTime createdOn;
    @Transient
    @Column(name = "published")
    private LocalDateTime publishedOn;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column
    @Enumerated(EnumType.STRING)
    private EventState state;
    @Column(name = "request_moderation")
    private boolean requestModeration;
}
