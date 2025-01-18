package com.pc.ecom.Service;

import com.pc.ecom.Model.Category;
import com.pc.ecom.Payload.CategoryDTO;

import java.util.List;

public interface CategoryService  {
    List<CategoryDTO> getAllCategories();
    void createCategory(Category category);
    String deleteCategory(Long Id);
    Category updateCategory(Category category, Long id);
}
