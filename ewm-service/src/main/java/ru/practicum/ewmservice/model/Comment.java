package ru.practicum.ewmservice.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;
    @Column(name = "comment_text")
    private String comment;
    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    @Column
    private LocalDateTime created;
    @Column
    private LocalDateTime updated;
    @Column
    private Boolean visible;
}
