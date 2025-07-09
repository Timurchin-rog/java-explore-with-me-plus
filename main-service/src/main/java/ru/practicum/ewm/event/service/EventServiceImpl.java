package ru.practicum.ewm.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.ViewDto;
import ru.practicum.dto.NewHitDto;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.event.*;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.PrivateEventParam;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.repository.LocationRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.exception.WrongTimeEventException;
import ru.practicum.ewm.request.RequestMapper;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.QRequest;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestState;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.user.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;
    private final JPAQueryFactory queryFactory;

    private static String datePattern = "yyyy-MM-dd HH:mm:ss";
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);

    @Override
    public List<EventFullDto> getEventsOfUser(PrivateEventParam param) {
        QEvent qEvent = QEvent.event;
        List<BooleanExpression> conditions = new ArrayList<>();

        conditions.add(QEvent.event.initiator.id.eq(param.getUserId()));

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(param.getFrom(), param.getSize(), sortById);

        List<EventFullDto> events = EventMapper.mapToEventFullDto(eventRepository.findAll(finalCondition, page));

        List<String> uris = events.stream()
                .map(event -> "/events/" + event.getId())
                .toList();

        List<ViewDto> views = statsClient.getStats(
                "2020-05-05 00:00:00",
                "2035-05-05 00:00:00",
                uris,
                false);

        events = events.stream()
                .peek(event -> {
                    Optional<Integer> countViews = views.stream()
                            .filter(view -> view.getUri().contains(event.getId().toString()))
                            .map(ViewDto::getHits)
                            .findFirst();
                    if (countViews.isEmpty()) {
                        event.setViews(0);
                    } else {
                        event.setViews(countViews.get());
                    }
                })
                .toList();

        return events;
    }

    @Override
    public EventFullDto getEventOfUser(PrivateEventParam param) {
        EventFullDto event = EventMapper.mapToEventFullDto(
                eventRepository.findByIdAndInitiator_Id(param.getEventId(), param.getUserId()).orElseThrow(
                        () -> new NotFoundException(String.format("Событие id = %d не найдено", param.getEventId()))
                )
        );

        String uri = "/events/" + param.getEventId();

        List<ViewDto> views = statsClient.getStats(
                "2020-05-05 00:00:00",
                "2035-05-05 00:00:00",
                List.of(uri),
                false);

        if (views.isEmpty()) {
            event.setViews(0);
        } else {
            event.setViews(views.getFirst().getHits());
        }

        return event;
    }

    @Transactional
    @Override
    public EventFullDto createEvent(PrivateEventParam param) {
        NewEventDto eventFromRequest = param.getNewEvent();
        User user = userRepository.findById(param.getUserId()).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь id = %d не найден", param.getUserId()))
        );
        Long categoryId = eventFromRequest.getCategory();
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundException(String.format("Категория id = %d не найдена", categoryId))
        );
        Location location = locationRepository.save(EventMapper.mapFromRequest(eventFromRequest.getLocation()));
        checkEventTime(eventFromRequest.getEventDate());
        Event newEvent = eventRepository.save(EventMapper.mapFromRequest(eventFromRequest));
        newEvent.setCategory(category);
        newEvent.setInitiator(user);
        newEvent.setLocation(location);
        return EventMapper.mapToEventFullDto(newEvent);
    }

    @Transactional
    @Override
    public EventFullDto updateEvent(PrivateEventParam param) {
        Event oldEvent = eventRepository.findByIdAndInitiator_Id(param.getEventId(), param.getUserId()).orElseThrow(
                () -> new NotFoundException(String.format("Событие id = %d не найдено", param.getEventId()))
        );
        if (oldEvent.getState().toString().equalsIgnoreCase("PUBLISHED")) {
            throw new ConflictException("Событие в публикации не может быть изменено");
        }
        UpdateEventUserRequest eventFromRequest = param.getEventOnUpdate();
        if (eventFromRequest.getEventDate() != null) {
            checkEventTime(eventFromRequest.getEventDate());
        }
        Event newEvent = EventMapper.updateEventFields(oldEvent, eventFromRequest);
        eventRepository.save(newEvent);
        return EventMapper.mapToEventFullDto(newEvent);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsOfUser(PrivateEventParam param) {
        QRequest qRequest = QRequest.request;
        List<BooleanExpression> conditions = new ArrayList<>();

        conditions.add(QRequest.request.requester.id.eq(param.getUserId()));
        conditions.add(QRequest.request.event.id.eq(param.getEventId()));

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        return RequestMapper.mapToRequestDto(requestRepository.findAll(finalCondition));
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateStatusOfRequests(PrivateEventParam param) {
        long userId = param.getUserId();
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException(String.format("Пользователь id = %d не найден", userId));
        }
        long eventId = param.getEventId();
        Event event = eventRepository.findByIdAndInitiator_Id(eventId, userId).orElseThrow(
                () -> new NotFoundException(String.format("Событие id = %d не найдено", eventId))
        );
        EventRequestStatusUpdateRequest requestOnUpdateStatus = param.getRequest();
        EventRequestStatusUpdateResult updatedRequests = EventRequestStatusUpdateResult.builder().build();

        if ((event.getParticipantLimit() == 0 || !event.getRequestModeration())
                && requestOnUpdateStatus.getStatus().equalsIgnoreCase("confirmed")) {
            return updatedRequests;
        }
        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Достигнут лимит запросов на участие в событии");
        }

        for (Long requestId : requestOnUpdateStatus.getRequestIds()) {
            Request request = requestRepository.findById(requestId).orElseThrow(
                    () -> new NotFoundException(String.format("Запрос id = %d не найден", requestId))
            );

            if (!request.getState().equals(RequestState.PENDING)) {
                throw new ConflictException("Статус можно изменить только у заявок, находящихся в ожидании");
            }

            if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
                request.setState(RequestState.REJECTED);
                requestRepository.save(request);
            }

            if (requestOnUpdateStatus.getStatus().equalsIgnoreCase("confirmed")) {
                request.setState(RequestState.CONFIRMED);
                requestRepository.save(request);
                event.setConfirmedRequests(+1L);
                eventRepository.save(event);
                updatedRequests.addConfirmedRequest(RequestMapper.mapToRequestDto(request));
            } else if (requestOnUpdateStatus.getStatus().equalsIgnoreCase("rejected")) {
                request.setState(RequestState.REJECTED);
                requestRepository.save(request);
                updatedRequests.addRejectedRequest(RequestMapper.mapToRequestDto(request));
            } else {
                throw new ValidationException("Заявки можно только подтверждать или отклонять");
            }
        }
        return updatedRequests;
    }

    @Override
    public Collection<EventShortDto> getPublicAllEvents(EventFilter filter, HttpServletRequest request) {
        log.info("параметры для фильтрации {}", filter);
        log.info("все события что есть в бд {}", eventRepository.findAll());
        checkFilterDateRangeIsGood(filter.getRangeStart(), filter.getRangeEnd());
        saveView(request);
        List<Event> events;
        QEvent event = QEvent.event;
        BooleanExpression exp;
        exp = event.state.eq(EventState.PUBLISHED);
        if (filter.getText() != null && !filter.getText().isBlank()) {
            exp = exp.and(event.description.containsIgnoreCase(filter.getText()))
                    .or(event.annotation.containsIgnoreCase(filter.getText()));
        }
        exp = exp.and(event.category.id.in(filter.getCategories()));
        exp = exp.and(event.paid.eq(filter.getPaid()));
        exp = exp.and(event.eventDate.after(filter.getRangeStart()));
        exp = exp.and(event.eventDate.before(filter.getRangeEnd()));
        if (filter.getOnlyAvailable()) {
            exp = exp.and(event.participantLimit.gt(event.confirmedRequests));
        }
        log.info("sql запрос к бд {}", exp);
        JPAQuery<Event> query = queryFactory.selectFrom(event)
                .where(exp)
                .offset(filter.getFrom())
                .limit(filter.getSize());
        events = query.fetch();
        log.info("события получаемы из бд после фильтрации {}", events);
        return events.stream()
                .map(event1 -> {
                    EventShortDto eventShortDto = EventMapper.mapToEventShortDto(event1);
                    eventShortDto.setViews(countView(event1.getId(), event1.getCreatedOn(), LocalDateTime.now()));
                    return eventShortDto;
                })
                .sorted((e1, e2) -> filter.getSort().equals("EVENT_DATE") ?
                        e1.getEventDate().compareTo(e2.getEventDate()) : e1.getViews().compareTo(e2.getViews()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Collection<EventFullDto> getAdminAllEvents(EventFilter filter) {
        log.debug("параметры для фильтрации {}", filter);
        log.debug("все события что есть в бд {}", eventRepository.findAll());
        checkFilterDateRangeIsGood(filter.getRangeStart(), filter.getRangeEnd());
        List<Event> events;
        QEvent event = QEvent.event;
        BooleanExpression exp;
        List<EventState> eventStates = filter.getStates().stream()
                .map(EventState::valueOf)
                .toList();
        exp = event.state.in(eventStates);
        exp = exp.and(event.initiator.id.in(filter.getUsers()));
        exp = exp.and(event.category.id.in(filter.getCategories()));
        exp = exp.and(event.eventDate.after(filter.getRangeStart()));
        exp = exp.and(event.eventDate.before(filter.getRangeEnd()));
        log.debug("sql запрос к бд {}", exp);
        JPAQuery<Event> query = queryFactory.selectFrom(event)
                .where(exp)
                .offset(filter.getFrom())
                .limit(filter.getSize());
        events = query.fetch();
        log.debug("события получаемы из бд после фильтрации {}", events);
        return events.stream()
                .map(event1 -> {
                    EventFullDto eventFullDto = EventMapper.mapToEventFullDto(event1);
                    eventFullDto.setViews(countView(event1.getId(), event1.getCreatedOn(), LocalDateTime.now()));
                    return eventFullDto;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public EventFullDto getPublicEvent(Long eventId, HttpServletRequest request) {
        final Event event = findEventById(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        } else {
            saveView(request);
        }

        final EventFullDto eventDto = EventMapper.mapToEventFullDto(event);
        eventDto.setViews(countView(event.getId(), event.getCreatedOn(), LocalDateTime.now()));
        return eventDto;
    }

    @Transactional
    @Override
    public EventFullDto updateByAdmin(Long eventId, UpdateEventUserRequest updateEvent) {
        Event event = findEventById(eventId);
        log.info("событие что мы нашли {}", event);
        log.info("сам запрос на обновление {}", updateEvent);
        validateEventDateForAdmin(updateEvent.getEventDate() == null ? event.getEventDate() :
                LocalDateTime.parse(updateEvent.getEventDate(), formatter), updateEvent.getStateAction());
        validateStatusForAdmin(event.getState(), updateEvent.getStateAction());
        if (updateEvent.getLocation() != null) {
            Location newLocation = locationRepository.save(EventMapper.mapFromRequest(updateEvent.getLocation()));
            log.info("сохранили новую локацию {}", newLocation);
            event.setLocation(newLocation);
        }
        EventMapper.updateEventFields(event, updateEvent);
        if (event.getState() != null && event.getState().equals(EventState.PUBLISHED)) {
            event.setPublishedOn(LocalDateTime.now());
        }
        eventRepository.save(event);
        log.info("обновленное событие {}", event);
        EventFullDto eventFullDto = EventMapper.mapToEventFullDto(event);
        eventFullDto.setViews(countView(event.getId(), event.getCreatedOn(), LocalDateTime.now()));
        return eventFullDto;
    }

    private void checkFilterDateRangeIsGood(LocalDateTime dateBegin, LocalDateTime dateEnd) {
        if (dateBegin == null) {
            return;
        }
        if (dateEnd == null) {
            return;
        }

        if (dateBegin.isAfter(dateEnd)) {
            throw new ValidationException(
                    "Неверно задана дата начала и конца события в фильтре");
        }
    }

    private Event findEventById(Long eventId) {

        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Публичное событие с id = " + eventId + " не найдено")
        );
    }

    private void saveView(HttpServletRequest request) {
        NewHitDto hitDto = NewHitDto.builder()
                .app("ewm-main-service")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        statsClient.registerHit(hitDto);
    }

    private Integer countView(Long eventId, LocalDateTime start, LocalDateTime end) {
        List<String> uris = List.of("/events/" + eventId);
        List<ViewDto> views = statsClient.getStats(start.format(formatter), end.format(formatter), uris, false);
        Optional<Integer> countViews = views.stream()
                .filter(view -> view.getUri().contains(eventId.toString()))
                .map(ViewDto::getHits)
                .findFirst();
        return countViews.orElse(0);
    }

    private void validateEventDateForAdmin(LocalDateTime eventDate, String stateAction) {
        if (stateAction != null && stateAction.equals("PUBLISH_EVENT") &&
                eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ConflictException("Прошло более часа с момента публикации события");
        }
    }

    private void validateStatusForAdmin(EventState eventState, String stateAction) {
        if (!eventState.equals(EventState.PENDING) && stateAction.equals("PUBLISH_EVENT")) {
            throw new ConflictException("Событие не в ожидании публикации");
        }
        if (eventState.equals(EventState.PUBLISHED) && stateAction.equals("REJECT_EVENT")) {
            throw new ConflictException("Нельзя отклонить опубликованное событие");
        }
    }

    private void checkEventTime(String eventDateStr) {
        String datePattern = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);

        LocalDateTime requestEventDate = LocalDateTime.parse(eventDateStr, formatter);
        Duration duration = Duration.between(LocalDateTime.now(), requestEventDate);
        Duration minDuration = duration.minusHours(2);
        if (minDuration.isNegative() && !minDuration.isZero()) {
            throw new WrongTimeEventException(
                    "Событие должно наступить минимум через 2 часа от момента добавления события");
        }
    }
}
