package org.example.mollyapi.product.service;

import lombok.RequiredArgsConstructor;
import org.example.mollyapi.product.entity.Category;
import org.example.mollyapi.product.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> findByName(String name) {
        return categoryRepository.findByCategoryName(name);
    }

    @Override
    public List<String> getCategoryPath(Category category) {
        List<String> path = new ArrayList<>();

        // 루프를 통해 카테고리의 족보를 찾음
        while (category != null) {
            path.add(category.getCategoryName());  // 현재 카테고리 이름을 추가
            category = category.getParent();  // 부모 카테고리로 이동
        }

        Collections.reverse(path);
        return path;
    }

    @Override
    public Category getCategory(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            throw new IllegalArgumentException("Categories cannot be empty");
        }

        String targetCategoryName = categories.get(categories.size() - 1);
        List<Category> categoriesList = findByName(targetCategoryName);

        for (Category category : categoriesList) {
            List<String> categoryPath = getCategoryPath(category);
            if (categories.equals(categoryPath)) {
                return category;
            }
        }

        throw new IllegalArgumentException("Category not found");
    }
}
