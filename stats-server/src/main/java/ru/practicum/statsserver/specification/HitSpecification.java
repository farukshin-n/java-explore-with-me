package ru.practicum.statsserver.specification;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.statsserver.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class HitSpecification {
    public static Specification<Hit> betweenDates(final LocalDateTime start, final LocalDateTime end) {
        return (hit, cq, cb) -> cb.between(hit.get("timestamp"), start, end);
    }

    public static Specification<Hit> uris(final List<String> uris) {
        return (hit, cq, cb) -> cb.in(hit.get("uri")).value(uris);
    }
}

