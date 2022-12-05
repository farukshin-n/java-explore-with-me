package ru.practicum.ewmservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmservice.model.ParticipationRequest;
import ru.practicum.ewmservice.model.ParticipationRequestState;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByRequester_Id(Long requesterId);

    List<ParticipationRequest> findAllByEvent_Id(Long eventId);

    List<ParticipationRequest> findAllByStatus(ParticipationRequestState status);

    Optional<ParticipationRequest> findByEvent_IdAndRequester_Id(Long eventId, Long userId);

    @Query("select pr from ParticipationRequest pr " +
            "where pr.event.id = ?1 " +
            "and pr.status = ?2")
    List<ParticipationRequest> findAllByEvent_IdAndStatus(Long eventId, ParticipationRequestState state);

    @Query("select count(pr) from ParticipationRequest pr " +
            "where pr.status = ?1 " +
            "and pr.event.id in ?2")
    List<Integer> countAllByStatusAndEvent_IdsIn(ParticipationRequestState state, List<Long> eventIds);
}
