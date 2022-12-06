package ru.practicum.statsserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.statsserver.dto.ViewStats;
import ru.practicum.statsserver.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long>, JpaSpecificationExecutor<Hit> {
    @Query(nativeQuery = true, name = "Hit.getAllUnique")
    List<ViewStats> getUniqueStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(nativeQuery = true, name = "Hit.getAll")
    List<ViewStats> getAllStats(LocalDateTime start, LocalDateTime end, List<String> uris);
}
