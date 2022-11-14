package ru.practicum.ewmservice.service;

import ru.practicum.ewmservice.dto.CategoryDto;
import ru.practicum.ewmservice.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto add(NewCategoryDto newCategory);

    List<CategoryDto> getAll(Integer from, Integer size);

    CategoryDto get(Long categoryId);

    CategoryDto update(CategoryDto newCategory);

    void delete(Long categoryId);
}
