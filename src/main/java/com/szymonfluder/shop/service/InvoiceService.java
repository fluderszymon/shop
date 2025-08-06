package com.szymonfluder.shop.service;

import com.szymonfluder.shop.dto.InvoiceDTO;

public interface InvoiceService {

    InvoiceDTO createInvoiceDTO(int orderId);
}
