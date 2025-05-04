package com.pc.ecom.Controller;

import com.pc.ecom.Model.Cart;
import com.pc.ecom.Payload.CartDTO;
import com.pc.ecom.Repository.CartRepository;
import com.pc.ecom.Service.CartService;
import com.pc.ecom.Utils.AuthUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/carts")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private AuthUtils authUtils;
    @Autowired
    private CartRepository cartRepository;

    @PostMapping("/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(
            @PathVariable Long productId,
            @PathVariable Integer quantity)
    {
        CartDTO cartDTO = cartService.addProductToCart(productId,quantity);

        return new ResponseEntity<>(cartDTO, HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getAllCarts(){
        List<CartDTO> cartDTOS = cartService.getAllCarts();
        return new ResponseEntity<>(cartDTOS, HttpStatus.FOUND);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCartByUserId(Long userId){
        String emailId = authUtils.loggedEmail();
        Cart cart = cartRepository.findCartByEmail(emailId);
        Long cartId = cart.getCartId();

        CartDTO cartDTO = cartService.getCart(emailId,cartId);
        return new ResponseEntity<>(cartDTO, HttpStatus.FOUND);
    }

    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(
            @PathVariable Long productId, @PathVariable String operation){

        CartDTO cartDTO = cartService.updateProductQuantityInCart(productId,
                operation.equalsIgnoreCase("delete")? -1:1);

        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(
            @PathVariable Long cartId,
            @PathVariable Long productId){
        String status = cartService.deleteProductFromCart(cartId,productId);

        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
