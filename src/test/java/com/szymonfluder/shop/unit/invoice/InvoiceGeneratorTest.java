package com.szymonfluder.shop.unit.invoice;

import com.szymonfluder.shop.dto.InvoiceDTO;
import com.szymonfluder.shop.dto.OrderItemDTO;
import com.szymonfluder.shop.invoice.InvoiceGenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceGeneratorTest {

    private final InvoiceGenerator invoiceGenerator = new InvoiceGenerator();

    private InvoiceDTO provideInvoiceDTO() {
        OrderItemDTO item1 = new OrderItemDTO(1, 1, 2, "Laptop", 1, 100.00);
        List<OrderItemDTO> orderItems = List.of(item1);
        return new InvoiceDTO("INV-001", LocalDate.now(), orderItems, 200.00, "User", "Address");
    }

    @Test
    void testGenerateInvoice_Success(@TempDir Path tempDir) throws Exception {
        String filePath = tempDir.resolve("test-invoice.pdf").toString();
        InvoiceDTO providedInvoiceDTO = provideInvoiceDTO();

        invoiceGenerator.generateInvoice(filePath, providedInvoiceDTO);

        File generatedFile = new File(filePath);
        assertTrue(generatedFile.exists());
        assertTrue(generatedFile.length() > 0);
    }

    @Test
    void testGenerateInvoice_NullInvoiceDTO(@TempDir Path tempDir) {
        String filePath = tempDir.resolve("null-invoice.pdf").toString();
        
        assertThrows(NullPointerException.class, () -> {
            invoiceGenerator.generateInvoice(filePath, null);
        });
    }
}