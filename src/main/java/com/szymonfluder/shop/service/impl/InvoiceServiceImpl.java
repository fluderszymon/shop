package com.szymonfluder.shop.service.impl;

import com.szymonfluder.shop.dto.InvoiceDTO;
import com.szymonfluder.shop.dto.OrderDTO;
import com.szymonfluder.shop.dto.OrderItemDTO;
import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.invoice.InvoiceGenerator;
import com.szymonfluder.shop.service.InvoiceService;
import com.szymonfluder.shop.service.OrderService;
import com.szymonfluder.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.util.List;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public InvoiceServiceImpl(OrderService orderService, UserService userService) {
        this.orderService = orderService;
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
        List<OrderItemDTO> orderItemDTOList = orderService.getAllOrderItemsByOrderId(orderId);
        UserDTO userDTO = userService.getUserById(orderDTO.getUserId());

        invoiceDTO.setInvoiceNumber("INV_" + orderDTO.getOrderId());
        invoiceDTO.setOrderItemDTOList(orderItemDTOList);
        invoiceDTO.setInvoiceDate(orderDTO.getOrderDate());
        invoiceDTO.setTotalPrice(calculateTotalPrice(orderItemDTOList));
        invoiceDTO.setUsername(userDTO.getUsername());
        invoiceDTO.setAddress(userDTO.getAddress());

        return invoiceDTO;
    }

    private BigDecimal calculateTotalPrice(List<OrderItemDTO> orderItemDTOList) {
        BigDecimal total = BigDecimal.valueOf(0.00).setScale(2, RoundingMode.HALF_UP);
        for (OrderItemDTO orderItemDTO : orderItemDTOList) {
            BigDecimal itemTotal = calculateOrderItemTotal(orderItemDTO);
            total = total.add(itemTotal);
        }
        return total;
    }

    private BigDecimal calculateOrderItemTotal(OrderItemDTO orderItemDTO) {
        return orderItemDTO.getPriceAtPurchase().multiply(BigDecimal.valueOf(orderItemDTO.getQuantity())).setScale(2, RoundingMode.HALF_UP);
    }
}