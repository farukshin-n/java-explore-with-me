package ru.practicum.statsserver.model;

import lombok.*;
import ru.practicum.statsserver.dto.ViewStats;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stats")
@NamedNativeQueries({
        @NamedNativeQuery(name = "Hit.getAllUnique",
                query = "SELECT app, uri, COUNT (DISTINCT ip) AS hits " +
                        "FROM stats " +
                        "WHERE timestamp BETWEEN ?1 AND ?2 " +
                        "AND uri IN ?3 " +
                        "GROUP BY app, uri ",
                resultSetMapping = "Mapping.ViewStats"),
        @NamedNativeQuery(name = "Hit.getAll",
                query = "SELECT app, uri, COUNT (ip) AS hits " +
                        "FROM stats " +
                        "WHERE timestamp BETWEEN ?1 AND ?2 " +
                        "AND uri IN ?3 " +
                        "GROUP BY app, uri ",
                resultSetMapping = "Mapping.ViewStats")
})
@SqlResultSetMapping(name = "Mapping.ViewStats",
        classes = @ConstructorResult(
                targetClass = ViewStats.class,
                columns = {@ColumnResult(name = "app"),
                        @ColumnResult(name = "uri"),
                        @ColumnResult(name = "hits", type = Long.class)}))
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hit_id")
    private Long id;
    @Column
    private String app;
    @Column
    private String uri;
    @Column
    private String ip;
    @Column
    private LocalDateTime timestamp;
}
