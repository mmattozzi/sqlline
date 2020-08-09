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
import java.util.List;

public class XlsSingleQueryOutputCollector extends SingleQueryOutputCollector {

    protected File fileOutput;
    protected HSSFSheet xlsSheet;
    protected HSSFWorkbook xlsWorkbook;
    protected short rowIndex = 0;
    protected List<String> colNames = null;

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
                titleRow.createCell((short) (i - 1)).setCellValue(
                        new HSSFRichTextString(colInfo.getColumnName(i)));
                xlsSheet.setColumnWidth((short) (i - 1), (short) 4000);
            }
        }

        // Save all the data from the database table rows
        while (rs.next()) {
            HSSFRow dataRow = xlsSheet.createRow(rowIndex++);
            short colIndex = 0;
            for (String colName : colNames) {
                dataRow.createCell(colIndex++).setCellValue(
                        new HSSFRichTextString(rs.getString(colName)));
            }
        }

    }

    @Override
    public void close() throws IOException {
        // Write to disk
        xlsWorkbook.write(new FileOutputStream(fileOutput));
    }
}
