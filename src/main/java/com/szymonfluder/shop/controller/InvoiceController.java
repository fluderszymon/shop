package com.szymonfluder.shop.controller;

import com.szymonfluder.shop.dto.InvoiceDTO;
import com.szymonfluder.shop.service.InvoiceService;
import com.szymonfluder.shop.service.PdfService;
import com.szymonfluder.shop.service.impl.PdfServiceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final PdfService pdfService;
    private final InvoiceService invoiceService;

    public InvoiceController(PdfService pdfService, InvoiceService invoiceService) {
        this.pdfService = pdfService;
        this.invoiceService = invoiceService;
    }

    @GetMapping("/{orderId}/pdf")
    public ResponseEntity<byte[]> generateInvoicePdf(@PathVariable int orderId) {
        try {
            InvoiceDTO invoiceDTO = invoiceService.createInvoiceDTO(orderId);
            if (invoiceDTO == null) {
                return ResponseEntity.notFound().build();
            }

            ByteArrayOutputStream outputStream = pdfService.generateInvoicePdf(invoiceDTO);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
//            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=query_results.pdf");
            headers.setContentLength(outputStream.size());

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }
}
