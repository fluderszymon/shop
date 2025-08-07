package com.szymonfluder.shop.invoice;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.szymonfluder.shop.dto.InvoiceDTO;
import com.szymonfluder.shop.dto.OrderItemDTO;
import com.szymonfluder.shop.util.SellerDetails;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.List;

public class InvoiceGenerator {

    private static final float FULL_WIDTH = 570f;
    private static final float[] ONE_COLUMN_WIDTH = {FULL_WIDTH/2};
    private static final float[] TWO_COLUMN_WIDTH = {FULL_WIDTH/2, FULL_WIDTH/2};
    private static final float[] HEADER_TABLE_WIDTH = {370f, 200f};
    private static final float[] NESTED_TABLE_IN_HEADER_WIDTH = {100f, 100f};
    private static final float[] SOLD_ITEMS_TABLE_COLUMNS_WIDTH = {55f, 275f, 60f, 80f, 100f};
    private static final float[] FULL_WIDTH_TABLE = {FULL_WIDTH};

    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.00");
    private static final Border THIN_GRAY_BORDER = new SolidBorder(ColorConstants.GRAY, 0.2f);
    private static final Border DASHED_GRAY_BORDER = new DashedBorder(ColorConstants.GRAY, 0.4f);

    public void generateInvoice(String filePath, InvoiceDTO invoiceDTO) throws FileNotFoundException {
        PdfWriter pdfWriter = new PdfWriter(filePath);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        pdfDocument.setDefaultPageSize(PageSize.A4);

        try (Document document = new Document(pdfDocument)) {
            addHeader(document, invoiceDTO);
            addDivider(document);
            addSellerAndCustomerInfo(document, invoiceDTO);
            addDivider(document);
            addSoldItemsTable(document, invoiceDTO);
            addTotalSummary(document, invoiceDTO);
        }
    }

    private void addHeader(Document document, InvoiceDTO invoiceDTO) {
        Table headerTable = new Table(HEADER_TABLE_WIDTH).setMarginBottom(5f);
        headerTable.addCell(createBoldCell("SHOP-PROJECT"));

        Table nestedTable = new Table(NESTED_TABLE_IN_HEADER_WIDTH);
        nestedTable.addCell(createBoldRightAlignedCell("Invoice Number: "));
        nestedTable.addCell(createLeftAlignedCell(invoiceDTO.getInvoiceNumber()));
        nestedTable.addCell(createBoldRightAlignedCell("Invoice Date: "));
        nestedTable.addCell(createLeftAlignedCell(String.valueOf(invoiceDTO.getInvoiceDate())));

        headerTable.addCell(new Cell().add(nestedTable).setBorder(Border.NO_BORDER));
        document.add(headerTable);
    }

    private void addDivider(Document document) {
        Table divider = new Table(FULL_WIDTH_TABLE).setMarginBottom(5f);
        divider.setBorder(THIN_GRAY_BORDER);
        document.add(divider);
    }

    private void addSellerAndCustomerInfo(Document document, InvoiceDTO invoiceDTO) {

        Table sellerAndCustomerInfoTableHeader = new Table(TWO_COLUMN_WIDTH);
        sellerAndCustomerInfoTableHeader.addCell(createSellerAndCustomerCell("Seller Information"));
        sellerAndCustomerInfoTableHeader.addCell(createSellerAndCustomerCell("Customer Information"));
        document.add(sellerAndCustomerInfoTableHeader.setMarginBottom(12f));

        Table sellerData = createInfoTable("Company", SellerDetails.COMPANY_NAME, "Address", SellerDetails.COMPANY_ADDRESS);
        Table customerData = createInfoTable("Name", invoiceDTO.getUserName(), "Address", invoiceDTO.getUserAddress());

        Table sellerAndBuyerInfoTableContent = new Table(TWO_COLUMN_WIDTH);
        sellerAndBuyerInfoTableContent.addCell(new Cell().add(sellerData).setBorder(Border.NO_BORDER));
        sellerAndBuyerInfoTableContent.addCell(new Cell().add(customerData).setBorder(Border.NO_BORDER));
        document.add(sellerAndBuyerInfoTableContent.setMarginBottom(12f));
    }

    private Table createInfoTable(String firstVal, String secondVal, String thirdVal, String fourthVal) {
        Table infoTable = new Table(ONE_COLUMN_WIDTH);
        infoTable.addCell(createLeftAligned10SizeCell(firstVal, true));
        infoTable.addCell(createLeftAligned10SizeCell(secondVal, false));
        infoTable.addCell(createLeftAligned10SizeCell(thirdVal, true));
        infoTable.addCell(createLeftAligned10SizeCell(fourthVal, false));
        return infoTable;
    }

    private void addSoldItemsTable(Document document, InvoiceDTO invoiceDTO) {

        Table soldItemsTableHeader = new Table(SOLD_ITEMS_TABLE_COLUMNS_WIDTH).setMarginTop(10f);
        String[] headers = {"Ord. no.", "Name", "Quantity", "Unit price", "Price [PLN]"};
        for (String header : headers) {
            soldItemsTableHeader.addCell(createCentredHeaderCell(header));
        }
        document.add(soldItemsTableHeader);

        Table soldItemsTableContent = new Table(SOLD_ITEMS_TABLE_COLUMNS_WIDTH);
        List<OrderItemDTO> orderItemDTOList = invoiceDTO.getOrderItemDTOList();

        for (int i = 0; i < orderItemDTOList.size(); i++) {
            OrderItemDTO orderItem = orderItemDTOList.get(i);
            double total = orderItem.getQuantity() * orderItem.getPriceAtPurchase();

            soldItemsTableContent.addCell(createCentredCell(String.valueOf(i+1)));
            soldItemsTableContent.addCell(createLeftAlignedCell(orderItem.getProductName()));
            soldItemsTableContent.addCell(createCentredCell(String.valueOf(orderItem.getQuantity())));
            soldItemsTableContent.addCell(createRightAlignedCell(String.valueOf(CURRENCY_FORMAT.format(orderItem.getPriceAtPurchase()))));
            soldItemsTableContent.addCell(createRightAlignedCell(String.valueOf(CURRENCY_FORMAT.format(total))));
        }
        document.add(soldItemsTableContent.setMarginBottom(20f));
    }

    private void addTotalSummary(Document document, InvoiceDTO invoiceDTO) {

        float[] totalTableWidth = {FULL_WIDTH-250f, 250f};
        Table totalTable = new Table(totalTableWidth);
        totalTable.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));

        Table dividerTable = new Table(FULL_WIDTH_TABLE);
        dividerTable.setBorder(DASHED_GRAY_BORDER);
        totalTable.addCell(new Cell().add(dividerTable).setBorder(Border.NO_BORDER));
        document.add(totalTable);

        Table totalRow = new Table(new float[] {330f, 70f, 170f});
        totalRow.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        totalRow.addCell(createLeftAlignedCell("Total"));
        totalRow.addCell(createRightAlignedCell(CURRENCY_FORMAT.format(invoiceDTO.getTotalPrice())));
        document.add(totalRow);
    }

    static Cell createBoldCell(String text) {
        return new Cell().add(new Paragraph(text).setBold()).setBorder(Border.NO_BORDER);
    }

    static Cell createLeftAlignedCell(String textValue) {
        return new Cell().add(new Paragraph(textValue)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
    }

    static Cell createBoldRightAlignedCell(String text) {
        return new Cell().add(new Paragraph(text).setBold()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
    }

    static Cell createRightAlignedCell(String text) {
        return new Cell().add(new Paragraph(text)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
    }

    static Cell createCentredCell(String text) {
        return new Cell().add(new Paragraph(text)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
    }

    static Cell createCentredHeaderCell(String text) {
        return new Cell().add(new Paragraph(text)).setBorder(THIN_GRAY_BORDER).setTextAlignment(TextAlignment.CENTER);
    }

    static Cell createSellerAndCustomerCell(String text) {
        return new Cell().add(new Paragraph(text).setFontSize(12f)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
    }

    static Cell createLeftAligned10SizeCell(String textValue, boolean isBold) {
        Cell cell = new Cell().add(new Paragraph(textValue).setFontSize(10f)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
        return isBold ?cell.setBold():cell;
    }
}