package com.szymonfluder.shop.service.impl;

import com.szymonfluder.shop.dto.InvoiceDTO;
import com.szymonfluder.shop.dto.OrderDTO;
import com.szymonfluder.shop.dto.OrderItemDTO;
import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.invoice.InvoiceGenerator;
import com.szymonfluder.shop.service.InvoiceService;
import com.szymonfluder.shop.service.OrderItemService;
import com.szymonfluder.shop.service.OrderService;
import com.szymonfluder.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final UserService userService;

    @Autowired
    public InvoiceServiceImpl(OrderService orderService, OrderItemService orderItemService, UserService userService) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
        this.userService = userService;
    }

    @Override
    public ByteArrayOutputStream generateInvoicePdf(InvoiceDTO invoiceDTO) throws IOException {
        return generatePdfFromInvoiceData(invoiceDTO);
    }

    private ByteArrayOutputStream generatePdfFromInvoiceData(InvoiceDTO invoiceDTO) throws IOException {
        String tempFilePath = "invoice_" + System.currentTimeMillis() + ".pdf";

        Path source = Path.of(tempFilePath);
        try {
            InvoiceGenerator invoiceGenerator = new InvoiceGenerator();
            invoiceGenerator.generateInvoice(tempFilePath, invoiceDTO);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            java.nio.file.Files.copy(
                    source,
                    outputStream
            );
            return outputStream;
        }
        finally {
            try {
                java.nio.file.Files.deleteIfExists(source);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public InvoiceDTO createInvoiceDTO(int orderId) {
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        OrderDTO orderDTO = orderService.getOrderById(orderId);
        List<OrderItemDTO> orderItemDTOList = orderItemService.getAllOrderItemsByOrderId(orderId);
        UserDTO userDTO = userService.getUserById(orderDTO.getUserId());

        invoiceDTO.setInvoiceNumber("INV_" + orderDTO.getOrderId());
        invoiceDTO.setOrderItemDTOList(orderItemDTOList);
        invoiceDTO.setInvoiceDate(orderDTO.getOrderDate());
        invoiceDTO.setTotalPrice(calculateTotalPrice(orderItemDTOList));
        invoiceDTO.setUserName(userDTO.getUsername());
        invoiceDTO.setUserAddress(userDTO.getAddress());

        return invoiceDTO;
    }

    private double calculateTotalPrice(List<OrderItemDTO> orderItemDTOList) {
        double total = 0;
        for (OrderItemDTO orderItemDTO : orderItemDTOList) {
            total += orderItemDTO.getPriceAtPurchase() * orderItemDTO.getQuantity();
        }
        return total;
    }
}
