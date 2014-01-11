package xml2xls;

import org.apache.poi.hssf.usermodel.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


class XLSCreator {
    
    private FileOutputStream _outputStream;
    private HSSFWorkbook _workbook = null;
    private HSSFSheet _sheet = null;
    private HSSFSheet _mainSheet = null;
    private HSSFRow _row = null;
    private HSSFRow _mainRow = null;
    private final HSSFCellStyle _cellStyle1;
    private HSSFCellStyle _cellStyle2 = null;

    private short _rowCounter = 0;
    private short _mainRowCounter = 0;
    
    private final HashMap<String,Integer> _sheets = new HashMap<String,Integer>();
        
    public XLSCreator(String fileName) {
        try {
            _outputStream = new FileOutputStream(fileName);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(XLSCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        _workbook = new HSSFWorkbook();
        
        _cellStyle1 = _workbook.createCellStyle();
        HSSFFont _font1 = _workbook.createFont();
        _cellStyle1.setFont(_font1);
        
        _cellStyle2 = _workbook.createCellStyle();
        HSSFFont _font2 = _workbook.createFont();
        _font2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        _cellStyle2.setFont(_font2);

        _mainSheet = _workbook.createSheet("Main");
        for(int i = 0; i< 9; i++) {
            _mainSheet.setColumnWidth(i, 4500);
        }
        _mainRowCounter = 0;
    }

    public void newRow(int delta) {
        _row = _sheet.createRow(_rowCounter +delta);
        _rowCounter += delta;
        _mainRow = _mainSheet.createRow(_mainRowCounter +delta);
        _mainRowCounter += delta;
    }

    public void writeCell(String text, int col, int style, boolean text_number) {
        HSSFCell _cell = _row.createCell(col);
        HSSFCell _mainCell = _mainRow.createCell(col);
        if(0 == style) {
            _cell.setCellStyle(_cellStyle1);
            _mainCell.setCellStyle(_cellStyle1);
        } else {
            _cell.setCellStyle(_cellStyle2);
            _mainCell.setCellStyle(_cellStyle2);
        } 
        if(text_number) {
            HSSFRichTextString richText = new HSSFRichTextString(text);
            _cell.setCellValue(richText);
            _mainCell.setCellValue(richText);
        } else {
            _cell.setCellValue(Double.parseDouble(text));
            _mainCell.setCellValue(Double.parseDouble(text));
        }
    }            

    public void newSheet(String name) {
        _sheet = _workbook.createSheet(name);
        for(int i = 0; i< 9; i++) {
            _sheet.setColumnWidth(i, 4500);
        }
        _rowCounter = 0;
    }

    public void setSheetName(int num, String name) {
        if (_sheets.containsKey(name)) {
            Integer suffix = _sheets.get(name);
            String newName = name.concat("_").concat(Integer.toString(suffix + 1));
            _sheets.put(name, suffix + 1);
            _workbook.setSheetName(num, newName);
        } else {
            _sheets.put(name, 1);
            String newName = name.concat("_1");
            _workbook.setSheetName(num, newName);
        }
    }

    public void close(){
        try {
            _workbook.write(_outputStream);
            _outputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(XLSCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
