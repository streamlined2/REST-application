package com.streamlined.restapp.service.reporter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.stream.Stream;

import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.streamlined.restapp.data.Person;
import com.streamlined.restapp.exception.ReportException;

import lombok.extern.slf4j.Slf4j;

/**
 * Class creates CSV report file and fills it with received person entities
 */

@Component
@Primary
@Slf4j
public class CSVReporter implements Reporter {

	private static final MediaType CSV_FILE_MEDIA_TYPE = new MediaType("text", "csv");
	private static final String RESULT_FILE_NAME = "workbook.csv";
	private static final String WORKBOOK_FILE_PREFIX = "workbook_";
	private static final String WORKBOOK_FILE_SUFFIX = ".csv";
	private static final String FIELD_SEPARATOR = ";";

	/**
	 * Method accepts stream of person entities and saves data as temporary file in
	 * CSV format
	 * 
	 * @param personStream stream of person entities
	 * @return created file in CSV format
	 * @throws ReportException if file cannot be created or filled in
	 */
	@Override
	public FileSystemResource getFileResource(Stream<Person> personStream) {
		try {
			Path file = Files.createTempFile(WORKBOOK_FILE_PREFIX, WORKBOOK_FILE_SUFFIX);
			fillInWorkbookFile(file, personStream);
			return new FileSystemResource(file);
		} catch (IOException e) {
			log.error("Error while creating workbook");
			throw new ReportException("Error while creating workbook", e);
		}
	}

	private void fillInWorkbookFile(Path file, Stream<Person> personStream) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(file, StandardOpenOption.WRITE,
				StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
			StringBuilder builder = new StringBuilder();
			for (Iterator<Person> i = personStream.iterator(); i.hasNext();) {
				Person person = i.next();
				builder.setLength(0);
				builder.append(person.getName()).append(FIELD_SEPARATOR);
				builder.append(person.getBirthday().format(DateTimeFormatter.ISO_DATE)).append(FIELD_SEPARATOR);
				builder.append(person.getSex().toString()).append(FIELD_SEPARATOR);
				builder.append(person.getEyeColor().toString()).append(FIELD_SEPARATOR);
				builder.append(person.getHeight());
				writer.write(builder.toString());
				writer.newLine();
			}
		}
	}

	@Override
	public MediaType getMediaType() {
		return CSV_FILE_MEDIA_TYPE;
	}

	@Override
	public String getFileName() {
		return RESULT_FILE_NAME;
	}

}
