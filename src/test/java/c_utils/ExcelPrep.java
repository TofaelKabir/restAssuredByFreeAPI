package c_utils;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

public class ExcelPrep {
    private File file;
    private FileInputStream fileInputStream;
    private FileOutputStream fileOutputStream;
    private Workbook workbook;
    public Sheet sheet;
    public int firstRow;
    private DataPrep dataPrep = new DataPrep();

    public ExcelPrep() {
        getExcel();
        loadExcel();
    }

/*	public static void main(String[] args) {
		loadExcel();
		for(String s: dataTable(63).keySet()) {
			System.out.println(s+":"+dataTable(63).get(s));
		}
	}*/

    private File getExcel() {
        for (File sfile : dataPrep.getFiles()) {
            try {
                String excelPath = sfile.getCanonicalPath();
                if (excelPath.contains(dataPrep.getConfigData().get(Fields.FILE.toString()))) {
                    file = sfile;
                    break;
                }
            } catch (Exception e) {
            }
        }
        return file;
    }

    // to prepare the excel sheet
    public boolean loadExcel() {
        try {
            getExcel();
            fileInputStream = new FileInputStream(file);
            workbook = WorkbookFactory.create(fileInputStream);
            String sheetName = dataPrep.getConfigData().get(Fields.SHEET.toString());
            sheet = workbook.getSheet(sheetName);
            firstRow = Integer.parseInt(dataPrep.getConfigData().get(Fields.ROW.toString()).trim()) - 1;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public HashMap<String, String> dataTable(int rowNum) {
        HashMap<String, String> dataTable = new HashMap<String, String>();
        DataFormatter formatter = new DataFormatter();
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        if (file != null) {
            try {
                Row row = sheet.getRow(rowNum);
                for (int j = 0; j < sheet.getRow(rowNum).getLastCellNum(); j++) {
                    int dummy = 1;
                    Object key = dummy;
                    Object value = "";
                    try {
                        key = sheet.getRow(firstRow).getCell(j);
                    } catch (NullPointerException e) {
                        dummy++;
                    }
                    try {
                        Cell cell = row.getCell(j);
                        evaluator.evaluate(cell);
                        value = formatter.formatCellValue(cell, evaluator);
						/*if(String.valueOf(key).equalsIgnoreCase("PolicyNum")) {

						}*/
                    } catch (NullPointerException e) {
                    }
                    String temp = String.valueOf(value);
                    String finalValue = "";
                    if (temp.contains(".0")) {
                        finalValue = temp.substring(0, temp.indexOf("."));
                    } else {
                        finalValue = temp;
                    }
                    dataTable.put(String.valueOf(key).trim(), finalValue.trim());
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
        return dataTable;
    }

    public void writeBackInExcel(int row, int column, String value) {
        try {
            fileInputStream = new FileInputStream(file);
            workbook = WorkbookFactory.create(fileInputStream);
            workbook.getSheet(dataPrep.getConfigData().get(Fields.SHEET.toString())).getRow(row).createCell(column).setCellValue(value);
            fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            workbook.close();
            fileInputStream.close();
            fileOutputStream.close();
            System.out.println("===:>>> Output Published <<<:===");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("***Error ocuure during writing***");
        }
    }

    public int getColumnNo(String columnName) {
        int col = 0;
        for (int i = 0; i < sheet.getRow(firstRow).getLastCellNum(); i++) {
            if (String.valueOf(sheet.getRow(firstRow).getCell(i)).trim().equalsIgnoreCase(columnName)) {
                col = i;
                break;
            }
        }
        return col;
    }

    public int getAvailableHeader() {
        int col = 0;
        for (int i = 0; i < sheet.getRow(firstRow).getLastCellNum(); i++) {
            if (String.valueOf(sheet.getRow(firstRow).getCell(i)).trim() != null &&
                    String.valueOf(sheet.getRow(firstRow).getCell(i)).trim().length() == 0) {
                col = i;
                break;
            } else if (String.valueOf(sheet.getRow(firstRow).getCell(i)).trim() == null) {
                col = i;
                break;
            }
        }
        if (col == 0 && String.valueOf(sheet.getRow(firstRow).getCell(sheet.getRow(firstRow).getLastCellNum() - 1)).trim().length() > 0) {
            col = sheet.getRow(firstRow).getLastCellNum();
        }
        return col;
    }

}
