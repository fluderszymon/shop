package com.szymonfluder.shop.integration.service;

import com.szymonfluder.shop.dto.InvoiceDTO;
import com.szymonfluder.shop.integration.config.TestConfig;
import com.szymonfluder.shop.mapper.CartItemMapperImpl;
import com.szymonfluder.shop.mapper.CartMapperImpl;
import com.szymonfluder.shop.mapper.OrderItemMapperImpl;
import com.szymonfluder.shop.mapper.OrderMapperImpl;
import com.szymonfluder.shop.mapper.ProductMapperImpl;
import com.szymonfluder.shop.mapper.UserMapperImpl;
import com.szymonfluder.shop.service.impl.CartServiceImpl;
import com.szymonfluder.shop.service.impl.InvoiceServiceImpl;
import com.szymonfluder.shop.service.impl.OrderServiceImpl;
import com.szymonfluder.shop.service.impl.ProductServiceImpl;
import com.szymonfluder.shop.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import({UserServiceImpl.class, UserMapperImpl.class, ProductServiceImpl.class,
        ProductMapperImpl.class, OrderServiceImpl.class, OrderMapperImpl.class,
        OrderItemMapperImpl.class, InvoiceServiceImpl.class, CartServiceImpl.class,
        CartMapperImpl.class, CartItemMapperImpl.class, TestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class InvoiceServiceImplTests extends AbstractServiceTest {

    @Test
    void createInvoiceDTO_shouldCreateInvoiceDTO() {
        addOrderToDatabase();
        InvoiceDTO actualInvoiceDTO = invoiceService.createInvoiceDTO(ORDER_ID);
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