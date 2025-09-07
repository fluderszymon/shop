package com.szymonfluder.shop.integration.service;

import com.szymonfluder.shop.dto.*;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.integration.config.TestConfig;
import com.szymonfluder.shop.mapper.*;
import com.szymonfluder.shop.security.JWTService;
import com.szymonfluder.shop.service.CartService;
import com.szymonfluder.shop.service.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.function.Executable;
import org.springframework.security.access.AccessDeniedException;

@DataJpaTest
@Import({UserServiceImpl.class, UserMapperImpl.class,
         ProductServiceImpl.class, ProductMapperImpl.class,
         CartServiceImpl.class, CartMapperImpl.class, CartItemMapperImpl.class,
         OrderServiceImpl.class, OrderMapperImpl.class, OrderItemMapperImpl.class,
         InvoiceServiceImpl.class, TestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class AbstractServiceTest {

    protected final int USER_ID = 1;
    protected final int PRODUCT_ID = 1;
    protected final int CART_ID = 1;
    protected final int CART_ITEM_ID = 1;
    protected final int ORDER_ID = 1;
    protected final int ORDER_ITEM_ID = 1;
    protected final int NON_EXISTING_ID = 999;

    protected final String USERNAME = "User";
    protected final String EMAIL = "user@outlook.com";
    protected final String PASSWORD = "password";
    protected final String ADDRESS = "Address";
    protected final String ROLE = "USER";

    protected final String OTHER_USERNAME = "OtherUser";
    protected final String OTHER_EMAIL = "other@outlook.com";
    protected final String ADMIN_ROLE = "ADMIN";
    protected final double INITIAL_BALANCE = 0.00;
    protected final double SUFFICIENT_BALANCE = 100.00;

    protected final String PRODUCT_NAME = "Product";
    protected final String UPDATED_PRODUCT_NAME = "Updated Product";
    protected final String PRODUCT_DESCRIPTION = "Description";
    protected final String UPDATED_PRODUCT_DESCRIPTION = "Updated Description";
    protected final double PRODUCT_PRICE = 10.00;
    protected final double UPDATED_PRODUCT_PRICE = 50.00;
    protected final int DEFAULT_STOCK = 100;
    protected final int UPDATED_STOCK = 200;
    protected final int DEFAULT_QUANTITY = 10;
    protected final int UPDATED_QUANTITY = 99;
    protected final int SMALL_QUANTITY = 5;

    protected final double CART_TOTAL = 100.0;
    protected final double ORDER_TOTAL = 100.0;

    @Autowired
    protected UserServiceImpl userService;
    
    @Autowired
    protected ProductServiceImpl productService;
    
    @Autowired
    protected CartService cartService;
    
    @Autowired
    protected OrderServiceImpl orderService;
    
    @Autowired
    protected InvoiceServiceImpl invoiceService;

    @Autowired
    protected JWTService jwtService;

    @Autowired
    protected ProductMapperImpl productMapper;

    protected void mockJwtService_getCurrentUsername() {
        when(jwtService.getCurrentUsername()).thenReturn(USERNAME);
    }

    protected void mockJwtService_getCurrentUsername_withWrongUsername() {
        when(jwtService.getCurrentUsername()).thenReturn(OTHER_USERNAME);
    }

    protected Product addProductToDatabase() {
        ProductCreateDTO productCreateDTO = new ProductCreateDTO(
                PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, DEFAULT_STOCK);
        return productService.addProduct(productCreateDTO);
    }

    protected Product getProductMock() {
        return new Product(PRODUCT_ID, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, DEFAULT_STOCK);
    }

    protected ProductDTO getProductDTOMock() {
        return new ProductDTO(PRODUCT_ID, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, DEFAULT_STOCK);
    }

    protected UserRegisterDTO getUserRegisterDTO() {
        return new UserRegisterDTO(USERNAME, EMAIL, PASSWORD, ADDRESS);
    }

    protected User addUserToDatabase() {
        UserRegisterDTO userRegisterDTO = getUserRegisterDTO();
        return userService.addUser(userRegisterDTO);
    }

    protected UserDTO getUserDTOMock() {
        return new UserDTO(USER_ID, USERNAME, EMAIL, ROLE, USER_ID, ADDRESS, INITIAL_BALANCE);
    }

    protected void addUserToDatabaseWithSufficientBalance() {
        User addedUser = userService.addUser(getUserRegisterDTO());
        userService.updateUserBalance(addedUser.getUserId(), SUFFICIENT_BALANCE);
        userService.getUserById(addedUser.getUserId());
    }

    protected CartDTO addCartToDatabase() {
        userService.addUser(getUserRegisterDTO());
        return cartService.getCartById(CART_ID);
    }

    protected CartItemDTO addCartItemToDatabase() {
        userService.addUser(getUserRegisterDTO());
        addProductToDatabase();
        return cartService.addCartItem(new CartItemDTO(0, CART_ID, PRODUCT_ID, DEFAULT_QUANTITY));
    }

    protected CartDTO getCartDTOMock() {
        return new CartDTO(CART_ID, USER_ID);
    }

    protected CartItemDTO getCartItemDTOMock() {
        return new CartItemDTO(CART_ITEM_ID, CART_ID, PRODUCT_ID, DEFAULT_QUANTITY);
    }

    protected void addOrderToDatabase() {
        addCartItemToDatabase();
        userService.updateUserBalance(USER_ID, SUFFICIENT_BALANCE);
        mockJwtService_getCurrentUsername();
        orderService.checkout();
    }

    protected OrderDTO getOrderDTOMock() {
        return new OrderDTO(ORDER_ID, USER_ID, ORDER_TOTAL, LocalDate.now());
    }

    protected OrderItemDTO getOrderItemDTOMock() {
        return new OrderItemDTO(ORDER_ITEM_ID, ORDER_ID, DEFAULT_QUANTITY, PRODUCT_NAME, PRODUCT_ID, PRODUCT_PRICE);
    }

    protected void setupCompleteCartScenario() {
        addCartItemToDatabase();
        userService.updateUserBalance(USER_ID, SUFFICIENT_BALANCE);
        mockJwtService_getCurrentUsername();
    }

    protected void setupCartWithNotEnoughStockInProducts() {
        setupCompleteCartScenario();
        ProductDTO productDTO = productService.getProductById(PRODUCT_ID);
        productDTO.setStock(0);
        productService.updateProduct(productMapper.productDTOToProduct(productDTO));
    }
    
    protected void setupEmptyCartScenario() {
        addUserToDatabaseWithSufficientBalance();
        mockJwtService_getCurrentUsername();
    }
    
    protected void setupInsufficientBalanceScenario() {
        addCartItemToDatabase();
        mockJwtService_getCurrentUsername();
    }

    protected InvoiceDTO getInvoiceDTOMock() {
        OrderItemDTO orderItemDTO = getOrderItemDTOMock();
        return new InvoiceDTO(("INV_" + ORDER_ID), LocalDate.now(), List.of(orderItemDTO), ORDER_TOTAL, USERNAME, ADDRESS);
    }

    protected void assertRuntimeExceptionWithMessage(Executable executable, String expectedMessage) {
        RuntimeException exception = assertThrows(RuntimeException.class, executable);
        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    }

    protected void assertAccessDeniedException(Executable executable, String expectedMessage) {
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, executable);
        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    }
}