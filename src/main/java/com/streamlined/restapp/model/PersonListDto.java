package com.streamlined.restapp.model;

import java.util.List;

import lombok.Builder;

@Builder
public record PersonListDto(List<EssentialPersonDto> list, int totalPages) {
}
