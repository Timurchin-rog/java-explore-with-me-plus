package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.event.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.LocationRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.WrongTimeEventException;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private static String datePattern = "yyyy-MM-dd HH:mm:ss";
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);

    @Transactional
    @Override
    public EventFullDto createEvent(NewEventDto eventFromRequest, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Событие не может создать несуществующий пользователь"));
        Category category = categoryRepository.findById(eventFromRequest.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория события не найдена"));
        Location location = locationRepository.save(EventMapper.mapFromRequest(eventFromRequest.getLocation()));
        LocalDateTime requestEventDate = LocalDateTime.parse(eventFromRequest.getEventDate(), formatter);
        Duration duration = Duration.between(LocalDateTime.now(), requestEventDate);
        Duration minDuration = duration.minusHours(2);
        if (minDuration.isNegative() && !minDuration.isZero())
            throw new WrongTimeEventException("Событие должно наступить минимум через 2 часа от момента добавления события");
        Event newEvent = eventRepository.save(EventMapper.mapFromRequest(eventFromRequest));
        newEvent.setCategory(category);
        newEvent.setInitiator(user);
        newEvent.setLocation(location);
        return EventMapper.mapToEventFullDto(newEvent);
    }


}
