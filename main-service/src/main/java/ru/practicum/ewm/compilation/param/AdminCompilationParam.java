package ru.practicum.ewm.compilation.param;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminCompilationParam {
    long compId;
    NewCompilationDto compilationFromRequest;
}
