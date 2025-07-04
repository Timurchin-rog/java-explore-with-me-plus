package ru.practicum.ewm.compilation;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.event.*;

import java.util.HashSet;
import java.util.Set;

public class CompilationMapper {

    public static CompilationDto mapToCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(EventMapper.mapToEventShortDto(compilation.getEvents()))
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public static Set<CompilationDto> mapToCompilationDto(Iterable<Compilation> compilations) {
        Set<CompilationDto> compilationsResult = new HashSet<>();

        for (Compilation compilation : compilations) {
            compilationsResult.add(mapToCompilationDto(compilation));
        }

        return compilationsResult;
    }

    public static Compilation mapFromRequest(NewCompilationDto compilation, Set<Event> events) {
        return new Compilation(
                events,
                validatePinned(compilation.getPinned()),
                validateTitle(compilation.getTitle())
        );
    }

    private static Boolean validatePinned(Boolean pinned) {
        if (pinned == null) {
            pinned = false;
        }

        return pinned;
    }

    private static String validateTitle(String title) {
        if (title.length() > 50) {
            throw new ValidationException("Название подборки не может быть больше 50 символов");
        } else {
            return title;
        }
    }

    public static Compilation updateCompilationFields(Compilation compilation, UpdateCompilationRequest compilationFromRequest) {
        if (compilationFromRequest.hasPinned()) {
            compilation.setPinned(compilationFromRequest.getPinned());
        }

        if (compilationFromRequest.hasTitle()) {
            compilation.setTitle(validateTitle(compilationFromRequest.getTitle()));
        }

        return compilation;
    }
}