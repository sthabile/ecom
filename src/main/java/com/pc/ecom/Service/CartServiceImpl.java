package com.pc.ecom.Service;

import com.pc.ecom.Exceptions.APIException;
import com.pc.ecom.Exceptions.ResourceNotFoundException;
import com.pc.ecom.Model.Cart;
import com.pc.ecom.Model.CartItem;
import com.pc.ecom.Model.Product;
import com.pc.ecom.Payload.CartDTO;
import com.pc.ecom.Payload.ProductDTO;
import com.pc.ecom.Repository.CartItemRepository;
import com.pc.ecom.Repository.CartRepository;
import com.pc.ecom.Repository.ProductRepository;
import com.pc.ecom.Utils.AuthUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Cart cart = createCart();
        Long cartId = cart.getCartId();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product",productId,"ProductId"));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);

        if(cartItem != null){
            throw new APIException("Product" + product.getProductName() + "already in Cart");
        }

        if(product.getQuantity() == 0){
            throw new APIException("Product" + product.getProductName() + "not available");
        }

        if(product.getQuantity() < quantity){
            throw new APIException("Product" + product.getProductName() + "not enough. " +
                    "Make an order of less than or equal to " + quantity);
        }

        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        newCartItem.setCart(cart);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        cartItemRepository.save(newCartItem);

        product.setQuantity(product.getQuantity() - quantity);

        cart.setTotalPrice(cart.getTotalPrice() + product.getSpecialPrice()*quantity);

        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItemList = cart.getCartItems();
        cartItemList.add(newCartItem);

        Stream<ProductDTO> productDTOStream = cartItemList.stream()
                .map(cartItem1 -> {
                    ProductDTO productDTO = modelMapper.map(cartItem1.getProduct(), ProductDTO.class);
                    productDTO.setQuantity(cartItem1.getQuantity());
                    return productDTO;
                });

        cartDTO.setProducts(productDTOStream.toList());

        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();

        if(carts.isEmpty()){
            throw new APIException("No cart found");
        }
        List<CartDTO> cartDTOList = carts.stream().map(
                cart -> {
                    CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
                    Stream<ProductDTO> productDTOS = cart.getCartItems().stream().map(
                            cartItem -> modelMapper.map(cartItem.getProduct(), ProductDTO.class)
                    );
                    cartDTO.setProducts(productDTOS.toList());
                    return cartDTO;
                }
        ).toList();

        return cartDTOList;
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId,cartId);
        //Do some basic error checks and model mapping
        if(cart == null){
            throw new APIException("No cart found");
        }
        return modelMapper.map(cart, CartDTO.class);
    }

    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        String emailId = authUtils.loggedEmail();
        Cart userCart = cartRepository.findCartByEmail(emailId);
        Long cartId = userCart.getCartId();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart",cartId,"CartId"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product",productId,"CartId"));

        if(product.getQuantity() == 0){
            throw new APIException("Product" + product.getProductName() + "not available");
        }

        if(product.getQuantity() < quantity){
            throw new APIException("Product" + product.getProductName() + "not enough");
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);

        if(cartItem == null){
            throw new APIException("Product" + product.getProductName() + "not found in cart");
        }

        cartItem.setProductPrice(product.getSpecialPrice());
        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cartItem.setDiscount(product.getDiscount());

        cart.setTotalPrice(cart.getTotalPrice() + cartItem.getProductPrice() * quantity);
        cartRepository.save(cart);
        CartItem updatedCartItem = cartItemRepository.save(cartItem);

        if(updatedCartItem.getQuantity() == 0){
            cartItemRepository.delete(updatedCartItem);
        }

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItemList = cart.getCartItems();

        Stream<ProductDTO> productDTOStream = cartItemList.stream().map(item ->{
            ProductDTO productDTO = modelMapper.map(item, ProductDTO.class);
            productDTO.setQuantity(item.getQuantity());
            return productDTO;
        });

        cartDTO.setProducts(productDTOStream.toList());

        return cartDTO;
    }

    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart",cartId,"CartId"));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);

        if(cartItem == null){
            throw new ResourceNotFoundException("Product",productId,"CartId");
        }

        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));

        Product product = cartItem.getProduct();
        product.setQuantity(product.getQuantity() - cartItem.getQuantity());

        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId,productId);

        return "Product " + cartItem.getProduct().getProductName() + " removed from cart";
    }

    private Cart createCart(){
        Cart userCart = cartRepository.findCartByEmail(authUtils.loggedEmail());
        if(userCart !=null ){
            return userCart;
        }

        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtils.loggedInUser());

        return cartRepository.save(cart);
    }
}
