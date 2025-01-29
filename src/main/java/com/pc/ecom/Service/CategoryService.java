package com.pc.ecom.Service;

import com.pc.ecom.Model.Category;
import com.pc.ecom.Payload.CategoryResponse;

import java.util.List;

public interface CategoryService  {
    CategoryResponse getAllCategories();
    void createCategory(Category category);
    String deleteCategory(Long Id);
    Category updateCategory(Category category, Long id);
}
