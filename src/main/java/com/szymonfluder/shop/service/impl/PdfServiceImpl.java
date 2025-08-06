package com.szymonfluder.shop.service.impl;

import com.szymonfluder.shop.dto.InvoiceDTO;
import com.szymonfluder.shop.pdf_creation.GeneratePdf;
import com.szymonfluder.shop.service.PdfService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;

@Service
public class PdfServiceImpl implements PdfService {

    public ByteArrayOutputStream generateInvoicePdf(InvoiceDTO invoiceDTO) throws IOException {
        return generatePdfFromInvoiceData(invoiceDTO);
    }

    private ByteArrayOutputStream generatePdfFromInvoiceData(InvoiceDTO invoiceDTO) throws IOException {
        String tempFilePath = "invoice_" + System.currentTimeMillis() + ".pdf";

        Path source = Path.of(tempFilePath);
        try {
            GeneratePdf pdfGenerator = new GeneratePdf();

            pdfGenerator.generateInvoice(tempFilePath, invoiceDTO);

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

}
