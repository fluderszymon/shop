package com.szymonfluder.shop.integration.service;

import com.szymonfluder.shop.dto.*;
import com.szymonfluder.shop.entity.Order;
import com.szymonfluder.shop.entity.OrderItem;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.mapper.*;
import com.szymonfluder.shop.repository.OrderItemRepository;
import com.szymonfluder.shop.repository.OrderRepository;
import com.szymonfluder.shop.service.impl.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import({UserServiceImpl.class, UserMapperImpl.class, ProductServiceImpl.class,
        ProductMapperImpl.class, OrderServiceImpl.class, OrderMapperImpl.class,
        OrderItemMapperImpl.class, InvoiceServiceImpl.class, CartServiceImpl.class,
        CartMapperImpl.class, CartItemServiceImpl.class, CartItemMapperImpl.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class InvoiceServiceImplTests {

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private InvoiceServiceImpl invoiceService;

    private User addUserToDatabase() {
        return userService.addUser(new UserRegisterDTO("User", "user@outlook.com", "password", "Address"));
    }

    private Product addProductToDatabase() {
        return productService.addProduct(new ProductCreateDTO("Product", "Product Description", 10.0, 100));
    }

    private Order addOrderToDatabase() {
        User addedUser = addUserToDatabase();
        Product addedProduct = addProductToDatabase();
        Order order = orderRepository.save(new Order(0, addedUser, new ArrayList<>(), 0.0, LocalDate.now()));
        OrderItem orderItem = orderItemRepository.save(new OrderItem(0, order, addedProduct, 10, 10.00));
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        order.setOrderItems(orderItems);
        return orderRepository.save(order);
    }

    private int populateDatabase() {
        Order addedOrder = addOrderToDatabase();
        return addedOrder.getOrderId();
    }

    private InvoiceDTO getInvoiceDTOMock() {
        List<OrderItemDTO> orderItemDTOList = List.of(new OrderItemDTO(1, 1, 10, "Product", 1, 10.00));
        return new InvoiceDTO(("INV_" + 1), LocalDate.now(), orderItemDTOList, 100.00, "User", "Address");
    }

    @Test
    void createInvoiceDTO_shouldCreateInvoiceDTO() {
        int orderId = populateDatabase();
        InvoiceDTO actualInvoiceDTO = invoiceService.createInvoiceDTO(orderId);
        InvoiceDTO expectedInvoiceDTO = getInvoiceDTOMock();

        assertThat(actualInvoiceDTO).isEqualTo(expectedInvoiceDTO);
    }

    @Test
    void generateInvoicePDF_shouldGenerateInvoicePDF() throws IOException {
        ByteArrayOutputStream result = invoiceService.generateInvoicePdf(getInvoiceDTOMock());

        byte[] pdfBytes = result.toByteArray();
        String header = new String(pdfBytes, 0, 5, StandardCharsets.US_ASCII);
        assertThat(header).isEqualTo("%PDF-");

        assertThat(result.size() > 0).isTrue();
    }
}