package com.pc.ecom.Repository;

import com.pc.ecom.Model.Category;
import com.pc.ecom.Model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findProductByCategory(Category category, Pageable pageRequest);

    Page<Product> findProductByProductNameLikeIgnoreCase(String keyword,Pageable pageRequest);
}
