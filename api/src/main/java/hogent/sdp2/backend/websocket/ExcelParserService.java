package hogent.sdp2.backend.websocket;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses an uploaded Excel (.xlsx) into structured text that Benoit can reason about.
 * Outputs every row and cell as-is in a markdown table, so the LLM sees the full structure
 * including metadata rows, headers, and data rows.
 */
@Service
public class ExcelParserService {

    public record ParsedSheet(String sheetName, int colCount, List<List<String>> rows) {}

    public List<ParsedSheet> parse(byte[] excelBytes) throws IOException {
        List<ParsedSheet> sheets = new ArrayList<>();

        try (var workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            DataFormatter formatter = new DataFormatter();

            for (int s = 0; s < workbook.getNumberOfSheets(); s++) {
                Sheet sheet = workbook.getSheetAt(s);
                if (sheet.getPhysicalNumberOfRows() == 0) continue;

                // Determine max column count across all rows
                int maxCols = 0;
                for (int r = 0; r <= sheet.getLastRowNum(); r++) {
                    Row row = sheet.getRow(r);
                    if (row != null && row.getLastCellNum() > maxCols) {
                        maxCols = row.getLastCellNum();
                    }
                }

                // Handle merged cells: build a lookup
                String[][] mergedValues = new String[sheet.getLastRowNum() + 1][maxCols];
                for (CellRangeAddress range : sheet.getMergedRegions()) {
                    Row firstRow = sheet.getRow(range.getFirstRow());
                    if (firstRow == null) continue;
                    Cell firstCell = firstRow.getCell(range.getFirstColumn());
                    String val = firstCell != null ? formatter.formatCellValue(firstCell).trim() : "";
                    for (int r = range.getFirstRow(); r <= range.getLastRow(); r++) {
                        for (int c = range.getFirstColumn(); c <= range.getLastColumn(); c++) {
                            if (r < mergedValues.length && c < mergedValues[r].length) {
                                mergedValues[r][c] = val;
                            }
                        }
                    }
                }

                List<List<String>> allRows = new ArrayList<>();
                for (int r = 0; r <= sheet.getLastRowNum(); r++) {
                    Row row = sheet.getRow(r);
                    List<String> cells = new ArrayList<>();
                    boolean hasData = false;

                    for (int c = 0; c < maxCols; c++) {
                        // Check merged cell value first
                        if (mergedValues[r][c] != null) {
                            cells.add(mergedValues[r][c]);
                            if (!mergedValues[r][c].isEmpty()) hasData = true;
                            continue;
                        }
                        Cell cell = row != null ? row.getCell(c) : null;
                        String value = cell != null ? formatter.formatCellValue(cell).trim() : "";
                        cells.add(value);
                        if (!value.isEmpty()) hasData = true;
                    }
                    if (hasData) allRows.add(cells);
                }

                if (!allRows.isEmpty()) {
                    sheets.add(new ParsedSheet(sheet.getSheetName(), maxCols, allRows));
                }
            }
        }

        return sheets;
    }

    /**
     * Converts parsed sheets to a markdown table so the LLM can clearly see the grid structure.
     */
    public String toText(List<ParsedSheet> sheets) {
        StringBuilder sb = new StringBuilder();
        for (ParsedSheet sheet : sheets) {
            sb.append("=== Sheet: ").append(sheet.sheetName()).append(" ===\n\n");

            for (int i = 0; i < sheet.rows().size(); i++) {
                List<String> row = sheet.rows().get(i);
                sb.append("| ");
                for (String cell : row) {
                    sb.append(cell.isEmpty() ? " " : cell).append(" | ");
                }
                sb.append("\n");

                // Add separator after first row
                if (i == 0) {
                    sb.append("| ");
                    for (int c = 0; c < row.size(); c++) {
                        sb.append("--- | ");
                    }
                    sb.append("\n");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
