package ru.practicum.ewm.category.open.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenCategoryServiceImpl implements OpenCategoryService {
    private final CategoryRepository repository;

    @Override
    public Collection<CategoryDto> getCategories(int from, int size) {
        Pageable page = PageRequest.of(from, size);
        List<Category> categories = repository.findAll(page).getContent();
        if (categories.isEmpty()) {
            return List.of();
        }
        return categories.stream()
                .map(CategoryMapper::mapToCategoryDto)
                .collect(Collectors.toSet());
    }

    @Override
    public CategoryDto getCategory(long catId) {
        Optional<Category> mayBeCategory = repository.findById(catId);
        if (mayBeCategory.isEmpty()) {
            log.debug("Категории с id {} не существует", catId);
            throw new NotFoundException();
        }
        return CategoryMapper.mapToCategoryDto(mayBeCategory.get());
    }

}
