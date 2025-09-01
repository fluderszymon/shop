package com.szymonfluder.shop.unit.controller;

import com.szymonfluder.shop.controller.InvoiceController;
import com.szymonfluder.shop.dto.InvoiceDTO;
import com.szymonfluder.shop.dto.OrderItemDTO;
import com.szymonfluder.shop.security.JWTService;
import com.szymonfluder.shop.security.SecurityConfig;
import com.szymonfluder.shop.security.UserDetailsServiceImpl;
import com.szymonfluder.shop.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InvoiceController.class)
@Import(SecurityConfig.class)
public class InvoiceControllerTests extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InvoiceService invoiceService;

    @MockitoBean
    private JWTService jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        setupJwtMocksWithTokenExtraction(jwtService, userDetailsService);
    }

    private InvoiceDTO provideInvoiceDTO() {
        List<OrderItemDTO> orderItems = List.of(new OrderItemDTO(1, 1, 2, "Product 1", 1, 20.00));
        return new InvoiceDTO("INV-001", LocalDate.now(), orderItems, 40.00, "John Doe", "123 Main St");
    }

    @Test
    void generateInvoicePdf_shouldReturnPdfFile() throws Exception {
        int orderId = 1;
        InvoiceDTO invoiceDTO = provideInvoiceDTO();
        
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        pdfStream.write("PDF content".getBytes());
        
        when(invoiceService.createInvoiceDTO(orderId)).thenReturn(invoiceDTO);
        when(invoiceService.generateInvoicePdf(any(InvoiceDTO.class))).thenReturn(pdfStream);

        mockMvc.perform(get("/invoices/{orderId}/pdf", orderId)
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Length", "11"));

        verify(invoiceService, times(1)).createInvoiceDTO(orderId);
        verify(invoiceService, times(1)).generateInvoicePdf(any(InvoiceDTO.class));
    }

    @Test
    void generateInvoicePdf_shouldReturnNotFound_whenInvoiceDTOIsNull() throws Exception {
        int orderId = 999;
        when(invoiceService.createInvoiceDTO(orderId)).thenReturn(null);

        mockMvc.perform(get("/invoices/{orderId}/pdf", orderId)
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isNotFound());

        verify(invoiceService, times(1)).createInvoiceDTO(orderId);
        verify(invoiceService, never()).generateInvoicePdf(any(InvoiceDTO.class));
    }

    @Test
    void generateInvoicePdf_shouldReturnInternalServerError_whenIOExceptionOccurs() throws Exception {
        int orderId = 1;
        InvoiceDTO invoiceDTO = provideInvoiceDTO();
        
        when(invoiceService.createInvoiceDTO(orderId)).thenReturn(invoiceDTO);
        when(invoiceService.generateInvoicePdf(any(InvoiceDTO.class)))
            .thenThrow(new IOException("PDF generation failed"));

        mockMvc.perform(get("/invoices/{orderId}/pdf", orderId)
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isInternalServerError());

        verify(invoiceService, times(1)).createInvoiceDTO(orderId);
        verify(invoiceService, times(1)).generateInvoicePdf(any(InvoiceDTO.class));
    }

    @Test
    void generateInvoicePdf_shouldHandleInvalidOrderIdFormat() throws Exception {
        mockMvc.perform(get("/invoices/invalid/pdf")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isBadRequest());
    }
}