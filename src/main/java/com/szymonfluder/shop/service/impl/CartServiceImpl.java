package com.szymonfluder.shop.service.impl;

import com.szymonfluder.shop.dto.CartDTO;
import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.entity.Cart;
import com.szymonfluder.shop.entity.CartItem;
import com.szymonfluder.shop.mapper.CartItemMapper;
import com.szymonfluder.shop.mapper.CartMapper;
import com.szymonfluder.shop.repository.CartItemRepository;
import com.szymonfluder.shop.repository.CartRepository;
import com.szymonfluder.shop.service.CartService;
import com.szymonfluder.shop.service.ProductService;
import com.szymonfluder.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductService productService;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    private final UserService userService;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository, CartMapper cartMapper,
                           ProductService productService, CartItemRepository cartItemRepository,
                           CartItemMapper cartItemMapper, UserService userService) {
        this.cartRepository = cartRepository;
        this.cartMapper = cartMapper;
        this.productService = productService;
        this.cartItemRepository = cartItemRepository;
        this.cartItemMapper = cartItemMapper;
        this.userService = userService;
    }

    // methods for "/carts" endpoint

    @Override
    public List<CartDTO> getAllCarts() {
        return cartRepository.findAll()
                .stream()
                .map(cartMapper::CartToCartDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CartDTO getCartById(int cartId) {
        Cart foundCart = cartRepository.findById(cartId).orElseThrow(() -> new RuntimeException("Cart not found"));
        return cartMapper.CartToCartDTO(foundCart);
    }

    @Override
    public CartDTO addCart(int userId) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setUserId(userId);
        Cart savedCart = cartRepository.save(cartMapper.CartDTOToCart(cartDTO));
        return cartMapper.CartToCartDTO(savedCart);
    }

    @Override
    public void deleteCartById(int cartId) {
        cartRepository.deleteById(cartId);
    }

    @Override
    public CartDTO updateCart(CartDTO cartDTO) {
        Optional<Cart> tempCart = cartRepository.findById(cartDTO.getCartId());
        CartDTO updatedCartDTO = new CartDTO();
        if(tempCart.isPresent()) {
            updatedCartDTO.setCartId(cartDTO.getCartId());
            updatedCartDTO.setUserId(cartDTO.getUserId());
        }
        Cart updatedCart = cartRepository.save(cartMapper.CartDTOToCart(updatedCartDTO));
        return cartMapper.CartToCartDTO(updatedCart);
    }

    @Override
    public double getCartTotal(int cartId) {
        ArrayList<CartItemDTO> cartItemDTOs = (ArrayList<CartItemDTO>) getAllCartItemsByCartId(cartId);
        double total = 0;
        for (CartItemDTO cartItemDTO : cartItemDTOs) {
            double productPrice = productService.getProductById(cartItemDTO.getProductId()).getPrice();
            total += cartItemDTO.getQuantity() * productPrice;
        }
        return total;
    }

    @Override
    public List<CartItemDTO> getAllCartItems() {
        return cartItemRepository.findAll()
                .stream()
                .map(cartItemMapper::cartItemToCartItemDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CartItemDTO> getAllCartItemsByCartId(int cartId) {
        return cartItemRepository.findAllCartItemsByCartId(cartId);
    }

    @Override
    public CartItemDTO getCartItemById(int cartItemId) {
        CartItem foundCartItem = cartItemRepository.findById(cartItemId).
                                    orElseThrow(() -> new RuntimeException("CartItem not found"));
        return cartItemMapper.cartItemToCartItemDTO(foundCartItem);
    }

    @Override
    public CartItemDTO addCartItem(CartItemDTO cartItemDTO) {
        if (!productService.isEnough(cartItemDTO.getProductId(), cartItemDTO.getQuantity())) {
            throw new RuntimeException("Not enough products in stock");
        }
        CartItem savedCartItem = cartItemRepository.save(cartItemMapper.cartItemDTOToCartItem(cartItemDTO));
        return cartItemMapper.cartItemToCartItemDTO(savedCartItem);
    }

    @Override
    public void deleteCartItemById(int cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    @Transactional
    @Override
    public CartItemDTO updateCartItem(CartItemDTO cartItemDTO) {
        Optional<CartItem> tempCartItem = cartItemRepository.findById(cartItemDTO.getCartItemId());
        CartItemDTO updatedCartItemDTO = new CartItemDTO();
        if(tempCartItem.isPresent()) {
            updatedCartItemDTO.setCartItemId(tempCartItem.get().getCartItemId());
            updatedCartItemDTO.setQuantity(cartItemDTO.getQuantity());
            updatedCartItemDTO.setCartId(cartItemDTO.getCartId());
            updatedCartItemDTO.setProductId(cartItemDTO.getProductId());
        }
        CartItem updatedCartItem = cartItemRepository.save(cartItemMapper.cartItemDTOToCartItem(updatedCartItemDTO));
        return cartItemMapper.cartItemToCartItemDTO(updatedCartItem);
    }

    // methods for "/my-cart" endpoint

    @Override
    public CartDTO getCartDTOForCurrentUser() {
        UserDTO currentUserDTO = userService.getCurrentUserDTO();
        return cartRepository.findCartDTOByUserId(currentUserDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Cart not found for current user"));
    }

    @Override
    public CartItemDTO getCartItemDTOForCurrentUserByCartItemId(int cartItemId) {
        validateCartItemOwnership(cartItemId);
        return getCartItemById(cartItemId);
    }

    @Override
    public List<CartItemDTO> getCartItemsInCartForCurrentUser() {
        CartDTO myCart = getCartDTOForCurrentUser();
        return getAllCartItemsByCartId(myCart.getCartId());
    }

    @Override
    public CartItemDTO addCartItemToCartForCurrentUser(CartItemDTO cartItemDTO) {
        validateCartItemOwnership(cartItemDTO);
        return addCartItem(cartItemDTO);
    }

    @Override
    public CartItemDTO updateCartItemInCartForCurrentUser(CartItemDTO cartItemDTO) {
        validateCartItemOwnership(cartItemDTO);
        return updateCartItem(cartItemDTO);
    }

    @Override
    public void deleteCartItemFromCartForCurrentUser(int cartItemId) {
        validateCartItemOwnership(cartItemId);
        deleteCartItemById(cartItemId);
    }

    @Override
    public double getCartTotalForCurrentUser() {
        CartDTO myCartDTO = getCartDTOForCurrentUser();
        return getCartTotal(myCartDTO.getCartId());
    }

    private void validateCartItemOwnership(int cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));
        
        validateCartOwnership(cartItem.getCart().getCartId());
    }

    private void validateCartItemOwnership(CartItemDTO cartItemDTO) {
        validateCartOwnership(cartItemDTO.getCartId());
    }
    
    private void validateCartOwnership(int cartId) {
        UserDTO currentUser = userService.getCurrentUserDTO();
        boolean isOwner = cartRepository.findCartDTOByUserId(currentUser.getUserId())
                .map(cart -> cart.getCartId() == cartId)
                .orElse(false);
                
        if (!isOwner) {
            throw new AccessDeniedException("You are not allowed to access this cart item");
        }
    }
}