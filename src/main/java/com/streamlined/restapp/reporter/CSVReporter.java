package com.streamlined.restapp.reporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.streamlined.restapp.exception.ReportException;
import com.streamlined.restapp.model.Person;

import lombok.extern.slf4j.Slf4j;

@Component
@Primary
@Slf4j
public class CSVReporter implements Reporter {

	private static final MediaType CSV_FILE_MEDIA_TYPE = new MediaType("text", "csv");
	private static final String RESULT_FILE_NAME = "workbook.csv";
	private static final String WORKBOOK_FILE_PREFIX = "workbook_";
	private static final String WORKBOOK_FILE_SUFFIX = ".csv";
	private static final String FIELD_SEPARATOR = ";";

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
		try (var writer = Files.newBufferedWriter(file, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING,
				StandardOpenOption.CREATE)) {
			StringBuilder builder = new StringBuilder();
			for (var i = personStream.iterator(); i.hasNext();) {
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
