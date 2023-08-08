package ru.practicum.explore_with_me.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore_with_me.request.model.Request;
import ru.practicum.explore_with_me.request.model.RequestStatuses;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequesterId(Long requesterId);

    Optional<Request> findByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<Request> findAllByEventIdInAndStatusEquals(List<Long> eventsIds, RequestStatuses status);

    Long countAllByEventIdAndStatusEquals(Long eventId, RequestStatuses status);

    List<Request> findAllByEventIdAndEventInitiatorId(Long eventId, Long initiatorId);
}
