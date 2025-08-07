package com.szymonfluder.shop.service;

import com.szymonfluder.shop.dto.InvoiceDTO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface InvoiceService {

    ByteArrayOutputStream generateInvoicePdf(InvoiceDTO invoiceDTO) throws IOException;
    InvoiceDTO createInvoiceDTO(int orderId);
}
