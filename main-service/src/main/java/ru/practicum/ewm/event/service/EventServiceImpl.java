package ru.practicum.ewm.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.ViewDto;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.PrivateEventParam;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.repository.LocationRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.WrongTimeEventException;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.user.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final StatsClient statsClient;

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
                    if (countViews.isEmpty())
                        event.setViews(0);
                    else
                        event.setViews(countViews.get());
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

        if (views.isEmpty())
            event.setViews(0);
        else
            event.setViews(views.getFirst().getHits());

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
        if (oldEvent.getState().toString().equalsIgnoreCase("PUBLISHED"))
            throw new ConflictException("Событие в публикации не может быть изменено");
        UpdateEventUserRequest eventFromRequest = param.getEventOnUpdate();
        if (eventFromRequest.getEventDate() != null)
            checkEventTime(eventFromRequest.getEventDate());
        Event newEvent = EventMapper.updateEventFields(oldEvent, eventFromRequest);
        eventRepository.save(newEvent);
        return EventMapper.mapToEventFullDto(newEvent);
    }

    private void checkEventTime(String eventDateStr) {
        String datePattern = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);

        LocalDateTime requestEventDate = LocalDateTime.parse(eventDateStr, formatter);
        Duration duration = Duration.between(LocalDateTime.now(), requestEventDate);
        Duration minDuration = duration.minusHours(2);
        if (minDuration.isNegative() && !minDuration.isZero())
            throw new WrongTimeEventException("Событие должно наступить минимум через 2 часа от момента добавления события");
    }
}
