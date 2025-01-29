package com.pc.ecom.Service;

import com.pc.ecom.Model.Category;
import com.pc.ecom.Payload.CategoryDTO;
import com.pc.ecom.Payload.CategoryResponse;


public interface CategoryService  {
    CategoryResponse getAllCategories();
    CategoryDTO createCategory(CategoryDTO categoryDto);
    CategoryDTO deleteCategory(Long Id);
    CategoryDTO updateCategory(CategoryDTO category, Long id);
}
