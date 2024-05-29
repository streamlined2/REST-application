package com.streamlined.restapp.dto;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;

public record ReportDto(FileSystemResource fileResource, String fileName, MediaType mediaType) {
}
