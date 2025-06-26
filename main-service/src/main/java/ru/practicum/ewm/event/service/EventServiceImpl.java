package ru.practicum.ewm.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.PrivateEventParam;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.repository.LocationRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.WrongTimeEventException;
import ru.practicum.ewm.user.QUser;
import ru.practicum.ewm.user.UserMapper;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.user.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;

    @Override
    public List<EventFullDto> getEventsOfUser(PrivateEventParam param) {
        QEvent event = QEvent.event;
        List<BooleanExpression> conditions = new ArrayList<>();

        conditions.add(QEvent.event.id.eq(param.getId()));

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(param.getFrom(), param.getSize(), sortById);

        return EventMapper.mapToEventFullDto(eventRepository.findAll(finalCondition, page));
    }

    @Transactional
    @Override
    public EventFullDto createEvent(NewEventDto eventFromRequest, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Событие не может создать несуществующий пользователь"));
        Category category = categoryRepository.findById(eventFromRequest.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория события не найдена"));
        Location location = locationRepository.save(EventMapper.mapFromRequest(eventFromRequest.getLocation()));
        checkEventTime(eventFromRequest);
        Event newEvent = eventRepository.save(EventMapper.mapFromRequest(eventFromRequest));
        newEvent.setCategory(category);
        newEvent.setInitiator(user);
        newEvent.setLocation(location);
        return EventMapper.mapToEventFullDto(newEvent);
    }

    private void checkEventTime(NewEventDto event) {
        String datePattern = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);

        LocalDateTime requestEventDate = LocalDateTime.parse(event.getEventDate(), formatter);
        Duration duration = Duration.between(LocalDateTime.now(), requestEventDate);
        Duration minDuration = duration.minusHours(2);
        if (minDuration.isNegative() && !minDuration.isZero())
            throw new WrongTimeEventException("Событие должно наступить минимум через 2 часа от момента добавления события");
    }
}
