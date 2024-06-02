package com.streamlined.restapp.service.reporter;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.stream.Stream;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.streamlined.restapp.data.Person;
import com.streamlined.restapp.exception.ReportException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class POIExcelReporter implements Reporter {

	private static final String WORKBOOK_FILE_PREFIX = "workbook_";
	private static final String WORKBOOK_FILE_SUFFIX = ".xls";
	private static final int BUFFER_SIZE = 8 * 1024;
	private static final MediaType EXCEL_FILE_MEDIA_TYPE = new MediaType("application", "vnd.ms-excel");
	private static final String RESULT_FILE_NAME = "workbook.xls";

	@Override
	public FileSystemResource getFileResource(Stream<Person> personStream) {
		try (Workbook workbook = new HSSFWorkbook()) {
			return new FileSystemResource(createWorkbookFile(workbook, personStream));
		} catch (IOException e) {
			log.error("Error while creating workbook");
			throw new ReportException("Error while creating workbook", e);
		}
	}

	private Path createWorkbookFile(Workbook workbook, Stream<Person> personStream) {
		try {
			Path file = Files.createTempFile(WORKBOOK_FILE_PREFIX, WORKBOOK_FILE_SUFFIX);
			Sheet sheet = workbook.createSheet();
			setColumnWidths(sheet);
			createHeader(workbook, sheet);
			createBody(workbook, sheet, personStream);
			workbook.write(new BufferedOutputStream(Files.newOutputStream(file, StandardOpenOption.WRITE,
					StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE), BUFFER_SIZE));
			return file;
		} catch (IOException e) {
			log.error("Error while creating workbook file");
			throw new ReportException("Error while creating workbook file", e);
		}
	}

	private void setColumnWidths(Sheet sheet) {
		sheet.setColumnWidth(0, 6000);
		sheet.setColumnWidth(1, 6000);
		sheet.setColumnWidth(2, 6000);
		sheet.setColumnWidth(3, 6000);
		sheet.setColumnWidth(4, 6000);
	}

	private void createHeader(Workbook workbook, Sheet sheet) {
		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		HSSFFont font = ((HSSFWorkbook) workbook).createFont();
		font.setFontName("Arial");
		font.setFontHeightInPoints((short) 16);
		font.setBold(true);
		headerStyle.setFont(font);

		Row header = sheet.createRow(0);

		Cell nameCell = header.createCell(0);
		nameCell.setCellValue("Name");
		nameCell.setCellStyle(headerStyle);

		Cell birthdayCell = header.createCell(1);
		birthdayCell.setCellValue("Birthday");
		birthdayCell.setCellStyle(headerStyle);

		Cell sexCell = header.createCell(2);
		sexCell.setCellValue("Sex");
		sexCell.setCellStyle(headerStyle);

		Cell eyeColorCell = header.createCell(3);
		eyeColorCell.setCellValue("Eye color");
		eyeColorCell.setCellStyle(headerStyle);

		Cell heightCell = header.createCell(4);
		heightCell.setCellValue("Height");
		heightCell.setCellStyle(headerStyle);
	}

	private void createBody(Workbook workbook, Sheet sheet, Stream<Person> personStream) {
		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		int index = 2;
		for (Iterator<Person> i = personStream.iterator(); i.hasNext(); index++) {
			createSheetRow(sheet, style, i.next(), index);
		}
	}

	private void createSheetRow(Sheet sheet, CellStyle style, Person person, int index) {
		Row row = sheet.createRow(index);

		Cell nameCell = row.createCell(0);
		nameCell.setCellValue(person.getName());
		nameCell.setCellStyle(style);

		Cell birthdayCell = row.createCell(1);
		birthdayCell.setCellValue(person.getBirthday());
		birthdayCell.setCellStyle(style);

		Cell sexCell = row.createCell(2);
		sexCell.setCellValue(person.getSex().toString());
		sexCell.setCellStyle(style);

		Cell eyeColorCell = row.createCell(3);
		eyeColorCell.setCellValue(person.getEyeColor().toString());
		eyeColorCell.setCellStyle(style);

		Cell heightCell = row.createCell(4);
		heightCell.setCellValue(person.getHeight().doubleValue());
		heightCell.setCellStyle(style);
	}

	@Override
	public MediaType getMediaType() {
		return EXCEL_FILE_MEDIA_TYPE;
	}

	@Override
	public String getFileName() {
		return RESULT_FILE_NAME;
	}

}
