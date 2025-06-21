package ru.practicum.ewm.category.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryRequest;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final CategoryRepository repository;

    @Transactional
    @Override
    public CategoryDto createCategory(NewCategoryRequest category) {
        mayBeDuplicateName(category.getName());
        Category newCategory = repository.save(CategoryMapper.mapFromRequest(category));
        log.debug("категория после добавления в бд {}", newCategory);
        return CategoryMapper.mapToCategoryDto(newCategory);
    }

    @Transactional
    @Override
    public void removeCategory(long catId) {
        //тут должна быть проверка на то существуют ли события, связанные с категорией. Если да то код 409
        mayBeCategory(catId);
        log.debug("удаляем категорию с id {}", catId);
        repository.deleteById(catId);
    }

    @Override
    public CategoryDto pathCategory(long catId, NewCategoryRequest category) {
        Category oldCategory = mayBeCategory(catId);
        if (oldCategory.getName().equals(category.getName())) {
            return CategoryMapper.mapToCategoryDto(oldCategory);
        }
        mayBeDuplicateName(category.getName());
        oldCategory.setName(category.getName());
        repository.save(oldCategory);
        return CategoryMapper.mapToCategoryDto(oldCategory);
    }

    private Category mayBeCategory(long id) {
        Optional<Category> mayBeCategory = repository.findById(id);
        if (mayBeCategory.isEmpty()) {
            log.debug("Категории с id = {} не существует", id);
            throw new NotFoundException();
        }
        return mayBeCategory.get();
    }

    private void mayBeDuplicateName(String name) {
        if (repository.findByNameLike(name).isPresent()) {
            log.debug("Название категории {} уже есть в базе", name);
            throw new ConflictException("Название категории " + name + "уже есть в базе");
        }
    }
}
