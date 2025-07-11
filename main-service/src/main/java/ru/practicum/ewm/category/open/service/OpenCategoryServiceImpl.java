package ru.practicum.ewm.category.open.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenCategoryServiceImpl implements OpenCategoryService {
    private final CategoryRepository repository;

    @Override
    public Collection<CategoryDto> getCategories(int from, int size) {
        Sort sortById = Sort.by("id").ascending();
        Pageable page = PageRequest.of(from, size, sortById);
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
        return CategoryMapper.mapToCategoryDto(repository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория " + catId + " не найдена")));
    }

}
