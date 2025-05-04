package com.pc.ecom.Service;

import com.pc.ecom.Payload.CartDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getCart(String emailId, Long cartId);

    CartDTO updateProductQuantityInCart(Long productId, Integer quantity);

    String deleteProductFromCart(Long cartId, Long productId);
}

