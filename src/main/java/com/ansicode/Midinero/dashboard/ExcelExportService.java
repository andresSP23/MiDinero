package com.ansicode.Midinero.dashboard;

import com.ansicode.Midinero.enums.TransactionType;
import com.ansicode.Midinero.transaction.Transaction;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelExportService {

    private final TransactionDashboardRepository transactionDashboardRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] exportTransactionsToExcel(Long userId, TransactionType type) throws IOException {

        List<Transaction> transactions = transactionDashboardRepository
                .findByUserIdAndTransactionTypeOrderByCreatedAtDesc(userId, type);

        String sheetName = type == TransactionType.INCOME ? "Ingresos" : "Gastos";

        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(sheetName);

            // --- Estilo de encabezado ---
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);

            // --- Estilo de moneda ---
            CellStyle currencyStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            currencyStyle.setDataFormat(format.getFormat("#,##0.00"));

            // --- Encabezados ---
            String[] headers = { "#", "Descripción", "Total", "Categoría", "Fecha" };
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // --- Datos ---
            int rowIdx = 1;
            for (Transaction tx : transactions) {
                Row row = sheet.createRow(rowIdx);

                row.createCell(0).setCellValue(rowIdx);

                row.createCell(1).setCellValue(tx.getDescription());

                Cell totalCell = row.createCell(2);
                totalCell.setCellValue(tx.getTotal().doubleValue());
                totalCell.setCellStyle(currencyStyle);

                row.createCell(3).setCellValue(
                        tx.getCategory() != null ? tx.getCategory().getName() : "");

                row.createCell(4).setCellValue(
                        tx.getCreatedAt() != null ? tx.getCreatedAt().format(DATE_FMT) : "");

                rowIdx++;
            }

            // Auto-ajustar columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
