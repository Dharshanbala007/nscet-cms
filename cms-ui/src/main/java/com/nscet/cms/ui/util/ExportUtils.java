package com.nscet.cms.ui.util;

import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ExportUtils {

    public static void exportToCsv(TableView<?> table, String defaultFileName, Window ownerWindow) {
        try {
            if (table == null) {
                showAlert("Export Warning", "No table available to export.", Alert.AlertType.WARNING);
                return;
            }

            Window window = ownerWindow;
            if (window == null && table.getScene() != null) {
                window = table.getScene().getWindow();
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save CSV File");
            fileChooser.setInitialFileName((defaultFileName != null ? defaultFileName.replaceAll("[^a-zA-Z0-9.-]", "_") : "Report") + ".csv");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv"));
            File file = fileChooser.showSaveDialog(window);

            if (file != null) {
                try (PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8)) {
                    List<TableColumn<?, ?>> columns = getLeafColumns(table.getColumns());

                    // Write Header
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < columns.size(); i++) {
                        String text = columns.get(i).getText();
                        sb.append("\"").append(cleanCsv(text != null ? text : "Column " + (i + 1))).append("\"");
                        if (i < columns.size() - 1) sb.append(",");
                    }
                    writer.println(sb.toString());

                    // Write Rows
                    int rowCount = table.getItems() != null ? table.getItems().size() : 0;
                    for (int row = 0; row < rowCount; row++) {
                        sb = new StringBuilder();
                        for (int colIdx = 0; colIdx < columns.size(); colIdx++) {
                            TableColumn col = columns.get(colIdx);
                            Object val = null;
                            try {
                                val = col.getCellData(row);
                            } catch (Exception ignored) {}
                            String strVal = val != null ? val.toString() : "";
                            sb.append("\"").append(cleanCsv(strVal)).append("\"");
                            if (colIdx < columns.size() - 1) sb.append(",");
                        }
                        writer.println(sb.toString());
                    }

                    showAlert("Export Success", "Successfully exported " + rowCount + " records to CSV file:\n" + file.getAbsolutePath(), Alert.AlertType.INFORMATION);
                }
            }
        } catch (Exception e) {
            String msg = (e.getMessage() != null && !e.getMessage().trim().isEmpty()) ? e.getMessage() : e.toString();
            showAlert("Export Error", "Failed to export CSV: " + msg, Alert.AlertType.ERROR);
        }
    }

    public static void exportToPdf(TableView<?> table, String title, Window ownerWindow) {
        try {
            if (table == null) {
                showAlert("Export Warning", "No table available to export.", Alert.AlertType.WARNING);
                return;
            }

            Window window = ownerWindow;
            if (window == null && table.getScene() != null) {
                window = table.getScene().getWindow();
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save PDF Document");
            fileChooser.setInitialFileName((title != null ? title.replaceAll("[^a-zA-Z0-9.-]", "_") : "Report") + ".pdf");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Documents (*.pdf)", "*.pdf"));
            File file = fileChooser.showSaveDialog(window);

            if (file != null) {
                List<TableColumn<?, ?>> columns = getLeafColumns(table.getColumns());
                List<String> headers = new ArrayList<>();
                for (TableColumn<?, ?> col : columns) {
                    headers.add(col.getText() != null ? col.getText() : "");
                }

                List<List<String>> rows = new ArrayList<>();
                int rowCount = table.getItems() != null ? table.getItems().size() : 0;
                for (int row = 0; row < rowCount; row++) {
                    List<String> rowData = new ArrayList<>();
                    for (TableColumn col : columns) {
                        Object val = null;
                        try {
                            val = col.getCellData(row);
                        } catch (Exception ignored) {}
                        rowData.add(val != null ? val.toString() : "");
                    }
                    rows.add(rowData);
                }

                byte[] pdfBytes = generateValidPdfBytes(title != null ? title : "Report Document", headers, rows);
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(pdfBytes);
                    fos.flush();
                }

                showAlert("Export Success", "Successfully exported PDF report document to:\n" + file.getAbsolutePath(), Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            String msg = (e.getMessage() != null && !e.getMessage().trim().isEmpty()) ? e.getMessage() : e.toString();
            showAlert("Export Error", "Failed to export PDF: " + msg, Alert.AlertType.ERROR);
        }
    }

    private static byte[] generateValidPdfBytes(String title, List<String> headers, List<List<String>> rows) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        List<String> pageStreams = new ArrayList<>();
        
        double pageWidth = 595.0;
        double startX = 40.0;
        double usableWidth = pageWidth - (startX * 2);
        
        int colCount = Math.max(1, headers.size());
        double colWidth = usableWidth / colCount;
        
        int totalRows = rows.size();
        int rowsPerPage = 28;
        int totalPages = Math.max(1, (int) Math.ceil((double) totalRows / (double) Math.max(1, rowsPerPage)));
        
        for (int p = 0; p < totalPages; p++) {
            StringBuilder content = new StringBuilder();
            
            // Header Title
            content.append("BT\n");
            content.append("/F1 13 Tf\n");
            content.append("1 0 0 1 65 800 Tm\n");
            content.append("(").append(pdfEscape("NADAR SARASWATHI COLLEGE OF ENGINEERING & TECHNOLOGY")).append(") Tj\n");
            content.append("ET\n");
            
            // Subtitle
            content.append("BT\n");
            content.append("/F1 11 Tf\n");
            content.append("1 0 0 1 ").append(startX).append(" 780 Tm\n");
            content.append("(").append(pdfEscape(title)).append(") Tj\n");
            content.append("ET\n");
            
            // Draw Table Header Background (Grey)
            double currentY = 760.0;
            double rowHeight = 18.0;
            
            content.append("0.9 0.9 0.95 rg\n");
            content.append(startX).append(" ").append(currentY - rowHeight).append(" ").append(usableWidth).append(" ").append(rowHeight).append(" re f\n");
            content.append("0 g\n");
            
            // Table Header Text & Borders
            for (int c = 0; c < colCount; c++) {
                double cx = startX + (c * colWidth);
                content.append("0.2 w\n");
                content.append(cx).append(" ").append(currentY - rowHeight).append(" ").append(colWidth).append(" ").append(rowHeight).append(" re s\n");
                
                content.append("BT\n");
                content.append("/F1 8 Tf\n");
                content.append("1 0 0 1 ").append(cx + 4).append(" ").append(currentY - 13).append(" Tm\n");
                String head = c < headers.size() ? headers.get(c) : "";
                if (head.length() > 18) head = head.substring(0, 15) + "...";
                content.append("(").append(pdfEscape(head)).append(") Tj\n");
                content.append("ET\n");
            }
            
            currentY -= rowHeight;
            
            // Table Rows
            int startRow = p * rowsPerPage;
            int endRow = Math.min(totalRows, startRow + rowsPerPage);
            
            for (int r = startRow; r < endRow; r++) {
                List<String> rowData = rows.get(r);
                
                if (r % 2 == 1) {
                    content.append("0.97 0.97 0.98 rg\n");
                    content.append(startX).append(" ").append(currentY - rowHeight).append(" ").append(usableWidth).append(" ").append(rowHeight).append(" re f\n");
                    content.append("0 g\n");
                }
                
                for (int c = 0; c < colCount; c++) {
                    double cx = startX + (c * colWidth);
                    content.append("0.1 w\n");
                    content.append(cx).append(" ").append(currentY - rowHeight).append(" ").append(colWidth).append(" ").append(rowHeight).append(" re s\n");
                    
                    content.append("BT\n");
                    content.append("/F1 7.5 Tf\n");
                    content.append("1 0 0 1 ").append(cx + 4).append(" ").append(currentY - 13).append(" Tm\n");
                    String val = c < rowData.size() ? rowData.get(c) : "";
                    if (val.length() > 22) val = val.substring(0, 19) + "...";
                    content.append("(").append(pdfEscape(val)).append(") Tj\n");
                    content.append("ET\n");
                }
                
                currentY -= rowHeight;
            }
            
            // Footer Page Number
            content.append("BT\n");
            content.append("/F1 8 Tf\n");
            content.append("1 0 0 1 250 25 Tm\n");
            content.append("(Page ").append(p + 1).append(" of ").append(totalPages).append(") Tj\n");
            content.append("ET\n");
            
            pageStreams.add(content.toString());
        }
        
        // Assemble PDF 1.4 Binary Structure
        List<Long> offsets = new ArrayList<>();
        
        baos.write("%PDF-1.4\n%\u00e2\u00e3\u00cf\u00d3\n".getBytes(StandardCharsets.ISO_8859_1));
        
        // Obj 1: Catalog
        offsets.add((long) baos.size());
        String obj1 = "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n";
        baos.write(obj1.getBytes(StandardCharsets.ISO_8859_1));
        
        // Obj 2: Pages
        int pageCount = pageStreams.size();
        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < pageCount; i++) {
            kids.append(3 + (i * 2)).append(" 0 R ");
        }
        offsets.add((long) baos.size());
        String obj2 = "2 0 obj\n<< /Type /Pages /Kids [" + kids.toString() + "] /Count " + pageCount + " >>\nendobj\n";
        baos.write(obj2.getBytes(StandardCharsets.ISO_8859_1));
        
        int fontObjNum = 3 + (pageCount * 2);
        
        for (int i = 0; i < pageCount; i++) {
            int pageObjNum = 3 + (i * 2);
            int streamObjNum = 4 + (i * 2);
            
            offsets.add((long) baos.size());
            String pageObj = pageObjNum + " 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 " + fontObjNum + " 0 R >> >> /Contents " + streamObjNum + " 0 R >>\nendobj\n";
            baos.write(pageObj.getBytes(StandardCharsets.ISO_8859_1));
            
            byte[] streamBytes = pageStreams.get(i).getBytes(StandardCharsets.ISO_8859_1);
            offsets.add((long) baos.size());
            String streamHeader = streamObjNum + " 0 obj\n<< /Length " + streamBytes.length + " >>\nstream\n";
            baos.write(streamHeader.getBytes(StandardCharsets.ISO_8859_1));
            baos.write(streamBytes);
            baos.write("\nendstream\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));
        }
        
        // Font Obj
        offsets.add((long) baos.size());
        String fontObj = fontObjNum + " 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>\nendobj\n";
        baos.write(fontObj.getBytes(StandardCharsets.ISO_8859_1));
        
        // XRef Table
        long xrefOffset = baos.size();
        int totalObjs = fontObjNum + 1;
        baos.write(("xref\n0 " + totalObjs + "\n").getBytes(StandardCharsets.ISO_8859_1));
        baos.write("0000000000 65535 f \n".getBytes(StandardCharsets.ISO_8859_1));
        
        for (Long off : offsets) {
            baos.write(String.format("%010d 00000 n \n", off).getBytes(StandardCharsets.ISO_8859_1));
        }
        
        // Trailer
        String trailer = "trailer\n<< /Size " + totalObjs + " /Root 1 0 R >>\nstartxref\n" + xrefOffset + "\n%%EOF\n";
        baos.write(trailer.getBytes(StandardCharsets.ISO_8859_1));
        
        return baos.toByteArray();
    }

    private static String pdfEscape(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)").replaceAll("[^\\x20-\\x7E]", "?");
    }

    private static List<TableColumn<?, ?>> getLeafColumns(List<? extends TableColumn<?, ?>> cols) {
        List<TableColumn<?, ?>> leafCols = new ArrayList<>();
        if (cols == null) return leafCols;
        for (TableColumn<?, ?> col : cols) {
            if (col.isVisible()) {
                if (col.getColumns().isEmpty()) {
                    leafCols.add(col);
                } else {
                    leafCols.addAll(getLeafColumns(col.getColumns()));
                }
            }
        }
        return leafCols;
    }

    private static String cleanCsv(String val) {
        if (val == null) return "";
        return val.replace("\"", "\"\"");
    }

    public static void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message != null && !message.trim().isEmpty() ? message : "Operation completed.");
        alert.showAndWait();
    }
}
