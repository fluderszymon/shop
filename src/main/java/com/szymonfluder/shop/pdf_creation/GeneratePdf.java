package com.szymonfluder.shop.pdf_creation;

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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GeneratePdf {

    private static final String INVOICE_FILE_PATH = "invoice.pdf";
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



    public static void main(String[] args) {
        try {
            generateInvoice();
        } catch (Exception e) {
            System.err.println("Error generating PDF: " + e.getMessage());
        }
    }

    public static void generateInvoice() throws FileNotFoundException {
        PdfWriter pdfWriter = new PdfWriter(INVOICE_FILE_PATH);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        pdfDocument.setDefaultPageSize(PageSize.A4);
        Document document = new Document(pdfDocument);

        try {
            addHeader(document);
            addDivider(document);
            addSellerAndCustomerInfo(document);
            addDivider(document);
            addSoldItemsTable(document);
            addTotalSummary(document);
//            addFinalDivider(document);
        } finally {
            document.close();
        }
    }

    private static void addHeader(Document document) {
        Table headerTable = new Table(HEADER_TABLE_WIDTH).setMarginBottom(5f);
        headerTable.addCell(createBoldCell("SHOP-PROJECT"));

        Table nestedTable = new Table(NESTED_TABLE_IN_HEADER_WIDTH);
        nestedTable.addCell(createBoldRightAlignedCell("Invoice Number: "));
        nestedTable.addCell(createLeftAlignedCell("234234234"));
        nestedTable.addCell(createBoldRightAlignedCell("Invoice Date: "));
        nestedTable.addCell(createLeftAlignedCell("11/10/2025"));

        headerTable.addCell(new Cell().add(nestedTable).setBorder(Border.NO_BORDER));
        document.add(headerTable);
    }

    private static void addDivider(Document document) {
        Table divider = new Table(FULL_WIDTH_TABLE).setMarginBottom(5f);
        divider.setBorder(THIN_GRAY_BORDER);
        document.add(divider);
    }

    private static void addSellerAndCustomerInfo(Document document) {

        Table sellerAndCustomerInfoTableHeader = new Table(TWO_COLUMN_WIDTH);
        sellerAndCustomerInfoTableHeader.addCell(createSellerAndCustomerCell("Seller Information"));
        sellerAndCustomerInfoTableHeader.addCell(createSellerAndCustomerCell("Customer Information"));
        document.add(sellerAndCustomerInfoTableHeader.setMarginBottom(12f));

        Table sellerData = createInfoTable("Company", "Shop-project", "Address", "924 New St. Santa Clara, CA 95051");
        Table customerData = createInfoTable("Name", "John Legend", "Address", "56 Canal Ave. Los Angeles, CA 90063");

        Table sellerAndBuyerInfoTableContent = new Table(TWO_COLUMN_WIDTH);
        sellerAndBuyerInfoTableContent.addCell(new Cell().add(sellerData).setBorder(Border.NO_BORDER));
        sellerAndBuyerInfoTableContent.addCell(new Cell().add(customerData).setBorder(Border.NO_BORDER));
        document.add(sellerAndBuyerInfoTableContent.setMarginBottom(12f));
    }

    private static Table createInfoTable(String firstVal, String secondVal, String thirdVal, String fourthVal) {
        Table infoTable = new Table(ONE_COLUMN_WIDTH);
        infoTable.addCell(createLeftAligned10SizeCell(firstVal, true));
        infoTable.addCell(createLeftAligned10SizeCell(secondVal, false));
        infoTable.addCell(createLeftAligned10SizeCell(thirdVal, true));
        infoTable.addCell(createLeftAligned10SizeCell(fourthVal, false));
        return infoTable;
    }

    private static void addSoldItemsTable(Document document) {

        Table soldItemsTableHeader = new Table(SOLD_ITEMS_TABLE_COLUMNS_WIDTH).setMarginTop(10f);
        String[] headers = {"Ord. no.", "Name", "Quantity", "Unit price", "Price [PLN]"};
        for (String header : headers) {
            soldItemsTableHeader.addCell(createCentredHeaderCell(header));
        }
        document.add(soldItemsTableHeader);

        List<OrderItem1> orderItems = createSampleOrderItems();
        Order1 order1 = new Order1(1235123, orderItems, 3456234.11f);

        Table soldItemsTableContent = new Table(SOLD_ITEMS_TABLE_COLUMNS_WIDTH);
        float totalSum = 0f;

        for (int i = 0; i < order1.getOrderItems().size(); i++) {
            OrderItem1 orderItem = orderItems.get(i);
            float total = orderItem.getQuantity() * orderItem.getPriceAtPurchase();
            totalSum += total;

            soldItemsTableContent.addCell(createCentredCell(String.valueOf(i+1)));
            soldItemsTableContent.addCell(createLeftAlignedCell(orderItem.getProductName()));
            soldItemsTableContent.addCell(createCentredCell(String.valueOf(orderItem.getQuantity())));
            soldItemsTableContent.addCell(createRightAlignedCell(String.valueOf(CURRENCY_FORMAT.format(orderItem.getPriceAtPurchase()))));
            soldItemsTableContent.addCell(createRightAlignedCell(String.valueOf(CURRENCY_FORMAT.format(total))));
        }
        document.add(soldItemsTableContent.setMarginBottom(20f));
    }

    private static List<OrderItem1> createSampleOrderItems() {
        List<OrderItem1> orderItems = new ArrayList<>();
        orderItems.add(new OrderItem1(1, 2, 10, "Item A", 50.99f));
        orderItems.add(new OrderItem1(2, 1, 66, "Item B", 63.12f));
        orderItems.add(new OrderItem1(3, 5, 22, "Item C", 12.34f));
        orderItems.add(new OrderItem1(4, 3, 48, "Item D", 24.00f));
        orderItems.add(new OrderItem1(5, 4, 33, "Item E", 77.50f));
        orderItems.add(new OrderItem1(6, 2, 90, "Item F", 9.99f));
        orderItems.add(new OrderItem1(7, 6, 71, "Item G", 88.88f));
        orderItems.add(new OrderItem1(8, 1, 10, "Item H", 3.45f));
        orderItems.add(new OrderItem1(9, 7, 11, "Item I", 13.45f));
        orderItems.add(new OrderItem1(10, 8, 95, "Item J", 56.78f));
        return orderItems;
    }

    private static void addTotalSummary(Document document) {

        List<OrderItem1> orderItems = createSampleOrderItems();
        float totalSum = 0f;
        for (OrderItem1 orderItem : orderItems) {
            totalSum += orderItem.getQuantity() * orderItem.getPriceAtPurchase();
        }
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
        totalRow.addCell(createRightAlignedCell(CURRENCY_FORMAT.format(totalSum)));
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

@Getter
@Setter
@AllArgsConstructor
class OrderItem1 {
    private int orderItemId;
    private int orderId;
    private int quantity;
    private String productName;
    private float priceAtPurchase;
}

@Getter
@Setter
@AllArgsConstructor
class Order1 {
    private int invoiceNumber;
    private List<OrderItem1> orderItems;
    private float total;

}