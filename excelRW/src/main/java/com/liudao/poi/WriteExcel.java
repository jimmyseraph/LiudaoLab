package com.liudao.poi;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WriteExcel {
	public static void main(String[] args) {
		// 创建Workbook对象，此处以XSSF为例
		Workbook wb = new XSSFWorkbook();
		/* 创建Sheet页，并给Sheet页命名，Excel对Sheet页命名有一定规则，一些特殊符号不可使用。
		 比如:\*?/[]这些符号都不能出现在Sheet页的名字中，所以可以使用createSafeSheetName方法
		 来生成安全的Sheet页名，把不支持的符号自动去掉。
		 */
		String safeName = WorkbookUtil.createSafeSheetName("Emp Info");
		Sheet sheet = wb.createSheet(safeName);
		Row tableNameRow = sheet.createRow(0); // 创建第一行，序号从0开始
		Cell tableNameCell = tableNameRow.createCell(0); // 在第一行中创建第一个单元格
		tableNameCell.setCellValue("Employee Information"); // 在第一格中填入字符串
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5)); // 合并单元格
		CellStyle style = wb.createCellStyle(); // 创建样式对象
		/*
		 * 设置字体为宋体，加粗，大小为24号
		 */
		Font font = wb.createFont();
		font.setFontName("宋体");
		font.setBold(true);
		font.setFontHeightInPoints((short)24);
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.CENTER); // 垂直居中
		style.setVerticalAlignment(VerticalAlignment.CENTER); // 水平居中
		style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex()); // 设置前景色为浅蓝
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND); // 前景色的填充方式
		tableNameCell.setCellStyle(style);
		
		/*
		 * 添加一行标题行
		 */
		font = wb.createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short)14);
		addRow(font,sheet,1,"emp_no","emp_name","emp_sex","hire_date","dept","salary"); // 创建第二行
		// 添加三条记录
		addRow(null,sheet,2,1,"Jimmy","male",to_date("2004-5-7"),"Marketing",5000.00); 
		addRow(null,sheet,3,2,"Sandy","female",to_date("2005-12-7"),"Account",7000.00);
		addRow(null,sheet,4,3,"Tommy","male",to_date("2014-5-12"),"Develop",4400.00);
		
		/*
		 * 设置工资总计的标题
		 */
		Row tableFootRow = sheet.createRow(5);
		Cell sumCell = tableFootRow.createCell(4);
		sumCell.setCellValue("Summary");
		style = wb.createCellStyle();
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.CENTER);
		sumCell.setCellStyle(style);
		/*
		 * 设置工资总计的数值单元格，使用公式来计算
		 */
		Cell sumResultCell = tableFootRow.createCell(5);
		sumResultCell.setCellFormula("sum(F3:F5)");
		style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setDataFormat((short)BuiltinFormats.getBuiltinFormat("0.00"));
		sumResultCell.setCellStyle(style);
		
		/*
		 * 调整每一列的宽度
		 */
		int lastCellNum = tableFootRow.getLastCellNum();
		for(int i = 0; i < lastCellNum; i++) {
			sheet.autoSizeColumn(i);
		}
		
		/*
		 * 关闭保存文件
		 */
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("excel/employee.xlsx");
			wb.write(fos);
			wb.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 用于添加一行数据
	 * @param font 这一行用的字体
	 * @param sheet 这一行所在的sheet页
	 * @param index 这一行在第几行，从0开始
	 * @param values 需要填入这一行单元格的值
	 */
	public static void addRow(Font font,Sheet sheet,int index,Object...values) {
		Row row = sheet.createRow(index);
		for(int i = 0; i < values.length; i++) {
			Cell cell = row.createCell(i);
			CellStyle s = sheet.getWorkbook().createCellStyle();
			s.setAlignment(HorizontalAlignment.CENTER); // 垂直居中
			s.setFont(font);
			// 根据输入参数的类型，转换成不同具体格式填入单元格
			if(values[i] instanceof Integer) {
				cell.setCellValue((int)values[i]);
			}else if(values[i] instanceof Double) {
				cell.setCellValue((double)values[i]);
				s.setDataFormat((short)BuiltinFormats.getBuiltinFormat("0.00"));
			}else if(values[i] instanceof Boolean) {
				cell.setCellValue((boolean)values[i]);
			}else if(values[i] instanceof Date) {
				cell.setCellValue((Date)values[i]);
				s.setDataFormat((short)BuiltinFormats.getBuiltinFormat("m/d/yy"));
			}else {
				cell.setCellValue(values[i].toString());
			}
			cell.setCellStyle(s);
		}
	}
	
	/**
	 * 根据字符串表达的日期，转换为Date类型
	 * @param date 用yyyy-MM-dd格式字符串表达的日期
	 * @return Date日期
	 */
	public static Date to_date(String date) {
		Date d = null;
		try {
			d = new SimpleDateFormat("yyyy-MM-dd").parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d;
	}
}
