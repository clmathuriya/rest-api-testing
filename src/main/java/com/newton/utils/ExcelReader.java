package com.newton.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {

	private Row row;

	public String[][] readExcelFile(String filepath, String sheetName) {
		try {

			FileInputStream file = new FileInputStream(new File(filepath));

			// Get the workbook instance for XLS file
			XSSFWorkbook workbook = new XSSFWorkbook(file);

			// Get first sheet from the workbook
			XSSFSheet sheet = workbook.getSheet(sheetName);

			// Iterate through each rows from first sheet
			Iterator<Row> rowIterator = sheet.iterator();
			row = rowIterator.next();
			int cells = row.getPhysicalNumberOfCells();
			String[][] sheetData = new String[sheet.getPhysicalNumberOfRows()][cells];
			// reset row iterator
			rowIterator = sheet.iterator();
			int i = 0, j = 0;

			while (rowIterator.hasNext()) {
				row = rowIterator.next();
				j = 0;

				// For each row, iterate through each columns
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext() && j < cells) {

					Cell cell = cellIterator.next();

					if (cell != null) {
						String cellValue = null;
						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_STRING:
							cellValue = cell.getStringCellValue();
							break;
						case Cell.CELL_TYPE_NUMERIC:
							cellValue = String.valueOf(cell.getNumericCellValue());
							break;
						case Cell.CELL_TYPE_BOOLEAN:
							cellValue = String.valueOf(cell.getBooleanCellValue());
							break;
						default:
						}
						sheetData[i][j++] = cellValue;

					}

				}
				i++;
			}
			file.close();

			return sheetData;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Object[][] getUserDataFromExcel(String fileName, String sheetName) {

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());

		String[][] excelData = readExcelFile(file.getAbsolutePath(), sheetName);
		int rowCount = 0;

		for (int i = 0; i < excelData.length; i++) {

			if (excelData[i][0] != null && excelData[i][0].equalsIgnoreCase("Yes")) {

				rowCount++;

			}

		}
		Object[][] dataSet = new Object[rowCount][1];
		int index = 0;

		for (int i = 0; i < excelData.length; i++) {
			Map<String, String> user = new HashMap<String, String>();

			if (excelData[i][0] != null && excelData[i][0].equalsIgnoreCase("Yes")) {

				for (int j = 0; j < excelData[i].length; j++) {

					if (excelData[0][j] != null && excelData[i][j] != null)

						user.put(excelData[0][j], excelData[i][j]);

				}
				Map<String, String> temp = user;
				dataSet[index++][0] = temp;

			}

		}

		return dataSet;
	}
}
