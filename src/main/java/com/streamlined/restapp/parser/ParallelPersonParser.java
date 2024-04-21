package com.streamlined.restapp.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterators;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.streamlined.restapp.exception.ParseException;
import com.streamlined.restapp.model.Person;

@Component("personParser")
public class ParallelPersonParser implements PersonParser {

	private static final String SOURCE_FILE_PATTERN = "*.json";
	private static final int SOURCE_FILE_QUEUE_INITIAL_CAPACITY = 100;
	private static final int RESULT_QUEUE_INITIAL_CAPACITY = 10_000;
	private static final int THREAD_COUNT = 4;

	private final ObjectMapper mapper;
	private final CollectionType collectionType;

	public ParallelPersonParser() {
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		collectionType = mapper.getTypeFactory().constructCollectionType(List.class, Person.class);
	}

	public Stream<Person> stream(Path path) {
		var iterator = new StreamingIterator(path);
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false).filter(Objects::nonNull);
	}

	private class StreamingIterator implements Iterator<Person> {

		private final ExecutorService executorService;
		private final AtomicInteger finishedThreadCount;
		private final BlockingQueue<Path> sourceFileQueue;
		private final BlockingQueue<Person> resultQueue;

		private StreamingIterator(Path path) {
			executorService = Executors.newCachedThreadPool();
			finishedThreadCount = new AtomicInteger(THREAD_COUNT);
			sourceFileQueue = new ArrayBlockingQueue<>(SOURCE_FILE_QUEUE_INITIAL_CAPACITY);
			resultQueue = new ArrayBlockingQueue<>(RESULT_QUEUE_INITIAL_CAPACITY);

			startParsing(path);
		}

		private void startParsing(Path dataPath) {
			try (var pathStream = Files.newDirectoryStream(dataPath, SOURCE_FILE_PATTERN)) {
				pathStream.forEach(sourceFileQueue::add);
				performParseTasks();
			} catch (IOException e) {
				throw new ParseException("Error iterating through directory %s".formatted(dataPath), e);
			}
		}

		private void performParseTasks() {
			for (int k = 0; k < THREAD_COUNT; k++) {
				executorService.submit(this::performParseTask);
			}
			executorService.shutdown();
		}

		private void performParseTask() {
			try {
				for (Path filePath = null; (filePath = sourceFileQueue.poll()) != null;) {
					parseFile(filePath);
				}
			} finally {
				finishedThreadCount.decrementAndGet();
			}
		}

		private void parseFile(Path filePath) {
			try (var reader = Files.newBufferedReader(filePath)) {
				List<Person> entities = mapper.readValue(reader, collectionType);
				for (var entity : entities) {
					resultQueue.put(entity);
				}
			} catch (IOException | InterruptedException e) {
				throw new ParseException("Error parsing file %s".formatted(filePath.toAbsolutePath()), e);
			}
		}

		private boolean isDone() {
			return finishedThreadCount.intValue() == 0 && resultQueue.isEmpty();
		}

		@Override
		public boolean hasNext() {
			return !isDone();
		}

		@Override
		public Person next() {
			if (isDone()) {
				throw new NoSuchElementException("No more elements left");
			}
			Person value = null;
			while (!isDone() && (value = resultQueue.poll()) == null) {
			}
			return value;
		}

	}

	public static void main(String... args) {
		var parser = new ParallelPersonParser();
		parser.stream(Path.of("src", "main", "resources", "data")).forEach(System.out::println);
	}

}