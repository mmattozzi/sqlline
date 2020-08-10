package sqlline;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XlsSingleQueryOutputCollector extends SingleQueryOutputCollector {


    public static final int DEFAULT_COL_WIDTH = 12;
    public static final int XLS_CHAR_PIXEL_WIDTH = 256;

    /** Maximum width of a column in characters (roughly). POI allows no higher than 255. */
    public static final int MAX_COL_WIDTH = 255;

    protected File fileOutput;
    protected HSSFSheet xlsSheet;
    protected HSSFWorkbook xlsWorkbook;
    protected int rowIndex = 0;
    protected List<String> colNames = null;
    protected Map<Integer, Integer> colMaxWidths = new HashMap<>();

    public XlsSingleQueryOutputCollector(File fileOutput) {
        this.fileOutput = fileOutput;

        // Create new Excel workbook and sheet
        xlsWorkbook = new HSSFWorkbook();
        xlsSheet = xlsWorkbook.createSheet();
    }

    @Override
    public void addResultSet(ResultSet rs) throws SQLException {

        if (colNames == null) {
            // Get the list of column names and store them as the first
            // row of the spreadsheet.
            ResultSetMetaData colInfo = rs.getMetaData();
            colNames = new ArrayList<>();
            HSSFRow titleRow = xlsSheet.createRow(rowIndex++);

            for (int i = 1; i <= colInfo.getColumnCount(); i++) {
                colNames.add(colInfo.getColumnName(i));
                titleRow.createCell((i - 1)).setCellValue(
                        new HSSFRichTextString(colInfo.getColumnName(i)));
                xlsSheet.setColumnWidth((i - 1), DEFAULT_COL_WIDTH * XLS_CHAR_PIXEL_WIDTH);
                colMaxWidths.put(i-1, 12);
            }
        }

        // Save all the data from the database table rows
        while (rs.next()) {
            HSSFRow dataRow = xlsSheet.createRow(rowIndex++);
            int colIndex = 0;
            for (String colName : colNames) {
                String value = rs.getString(colName);
                dataRow.createCell(colIndex).setCellValue(
                        new HSSFRichTextString(value));
                if (colMaxWidths.get(colIndex) < value.length()) {
                    if (value.length() < 254) {
                        xlsSheet.setColumnWidth(colIndex, (value.length() + 1)  * XLS_CHAR_PIXEL_WIDTH);
                    } else {
                        xlsSheet.setColumnWidth(colIndex, MAX_COL_WIDTH * XLS_CHAR_PIXEL_WIDTH);
                    }
                }
                colIndex++;
            }
        }

    }

    @Override
    public void close() throws IOException {
        // Write to disk
        xlsWorkbook.write(new FileOutputStream(fileOutput));
    }
}
