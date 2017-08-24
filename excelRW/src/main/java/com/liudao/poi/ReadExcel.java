package com.liudao.poi;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadExcel {
	public static void main(String[] args) {
		Workbook wb = null;
		try {
			wb = new XSSFWorkbook("excel/employee.xlsx");
			Sheet sheet = wb.getSheet("Emp Info");
			int lastRowNum = sheet.getLastRowNum(); // 最后一行的编号，从0开始
			for(int i = 1; i < lastRowNum; i++) {
				Row row = sheet.getRow(i);
				int lastCellNum = row.getLastCellNum(); // 第i行的最后一个单元格的编号，从1开始
				for(int j = 0; j < lastCellNum; j++) {
					Cell cell = row.getCell(j);
					/*
					 * 判断单元格中的数据是什么格式，根据格式打印
					 */
					switch(cell.getCellTypeEnum()) {
						case BOOLEAN:
							System.out.print(cell.getBooleanCellValue()+"\t");
							break;
						case NUMERIC:
							if(DateUtil.isCellDateFormatted(cell)) { //判断数字是否是日期
								System.out.print(new SimpleDateFormat("yyyy/MM/dd").format(cell.getDateCellValue())+"\t");
							}else {
								System.out.print(cell.getNumericCellValue()+"\t");
							}
							break;
						case STRING:
							System.out.print(cell.getStringCellValue()+"\t");
							break;
						case FORMULA:
							System.out.print(cell.getCellFormula()+"\t");
							break;
						default:
							System.out.print("err\t");
					}
				}
				System.out.println();
			}
			// 打印最后一行的汇总数据
			Row row = sheet.getRow(lastRowNum);
			System.out.print(row.getCell(row.getLastCellNum()-2).getStringCellValue()+": ");
			// 最后一个单元格是公式，将结果打印出来
			FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
			System.out.print(evaluator.evaluate(row.getCell(row.getLastCellNum()-1)).getNumberValue());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				wb.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
