package com.streamlined.restapp.reporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.streamlined.restapp.exception.ReportException;
import com.streamlined.restapp.model.Person;

@Component
@Primary
public class CSVReporter implements Reporter {

	private static final MediaType CSV_FILE_MEDIA_TYPE = new MediaType("text", "csv");
	private static final String RESULT_FILE_NAME = "workbook.csv";
	private static final String WORKBOOK_FILE_PREFIX = "workbook_";
	private static final String WORKBOOK_FILE_SUFFIX = ".csv";

	@Override
	public FileSystemResource getFileResource(Stream<Person> personStream) {
		try {
			Path file = Files.createTempFile(WORKBOOK_FILE_PREFIX, WORKBOOK_FILE_SUFFIX);
			fillInWorkbookFile(file, personStream);
			return new FileSystemResource(file);
		} catch (IOException e) {
			throw new ReportException("Error while creating workbook", e);
		}
	}

	private void fillInWorkbookFile(Path file, Stream<Person> personStream) throws IOException {
		try (var writer = Files.newBufferedWriter(file, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING,
				StandardOpenOption.CREATE)) {
			for (var i = personStream.iterator(); i.hasNext();) {
				Person person = i.next();
				writer.write("%s,%tF,%s,%s,%.2f%n".formatted(person.getName(), person.getBirthday(),
						person.getSex().toString(), person.getEyeColor().toString(), person.getHeight().doubleValue()));
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
