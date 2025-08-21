package com.szymonfluder.shop.unit.invoice;

import com.szymonfluder.shop.dto.InvoiceDTO;
import com.szymonfluder.shop.dto.OrderItemDTO;
import com.szymonfluder.shop.invoice.InvoiceGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceGeneratorTest {

    private InvoiceGenerator invoiceGenerator;
    private InvoiceDTO sampleInvoiceDTO;

    @BeforeEach
    void setUp() {
        invoiceGenerator = new InvoiceGenerator();
        
        OrderItemDTO item1 = new OrderItemDTO(1, 1, 2, "Laptop", 1, 100.00);
        OrderItemDTO item2 = new OrderItemDTO(2, 1, 1, "Mouse", 2, 200.00);
        OrderItemDTO item3 = new OrderItemDTO(3, 1, 3, "Keyboard", 3, 300.00);
        
        List<OrderItemDTO> orderItems = Arrays.asList(item1, item2, item3);
        
        sampleInvoiceDTO = new InvoiceDTO();
        sampleInvoiceDTO.setInvoiceNumber("INV-2024-001");
        sampleInvoiceDTO.setInvoiceDate(LocalDate.of(2024, 1, 15));
        sampleInvoiceDTO.setOrderItemDTOList(orderItems);
        sampleInvoiceDTO.setTotalPrice(600.00);
        sampleInvoiceDTO.setUserName("John Doe");
        sampleInvoiceDTO.setUserAddress("123 Main St, Somewhere, USA");
    }

    @Test
    void testGenerateInvoice_Success(@TempDir Path tempDir) throws Exception {
        String filePath = tempDir.resolve("test-invoice.pdf").toString();
        
        invoiceGenerator.generateInvoice(filePath, sampleInvoiceDTO);
        
        File generatedFile = new File(filePath);
        assertTrue(generatedFile.exists());
        assertTrue(generatedFile.length() > 0);
    }

    @Test
    void testGenerateInvoice_WithSingleOrderItem(@TempDir Path tempDir) throws Exception {
        String filePath = tempDir.resolve("single-item-invoice.pdf").toString();
        OrderItemDTO singleItem = new OrderItemDTO(1, 1, 1, "Single Product", 1, 100.00);
        
        InvoiceDTO singleItemInvoice = new InvoiceDTO();
        singleItemInvoice.setInvoiceNumber("INV-SINGLE-001");
        singleItemInvoice.setInvoiceDate(LocalDate.now());
        singleItemInvoice.setOrderItemDTOList(Collections.singletonList(singleItem));
        singleItemInvoice.setTotalPrice(100.00);
        singleItemInvoice.setUserName("Single Customer");
        singleItemInvoice.setUserAddress("789 Pine St, Nowhere, USA");
        
        invoiceGenerator.generateInvoice(filePath, singleItemInvoice);
        
        File generatedFile = new File(filePath);
        assertTrue(generatedFile.exists());
        assertTrue(generatedFile.length() > 0);
    }

    @Test
    void testGenerateInvoice_FileNotFound() {
        String invalidPath = "/invalid/path.pdf";
        
        assertThrows(FileNotFoundException.class, () -> {
            invoiceGenerator.generateInvoice(invalidPath, sampleInvoiceDTO);
        });
    }

    @Test
    void testGenerateInvoice_NullInvoiceDTO(@TempDir Path tempDir) {
        String filePath = tempDir.resolve("null-invoice.pdf").toString();
        
        assertThrows(NullPointerException.class, () -> {
            invoiceGenerator.generateInvoice(filePath, null);
        });
    }
}