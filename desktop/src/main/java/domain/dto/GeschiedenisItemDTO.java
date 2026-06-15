package domain.dto;

import java.time.LocalDate;

public record GeschiedenisItemDTO(
        Integer id,
        String type,
        LocalDate startDatum,
        LocalDate eindDatum,
        String status,
        String omschrijving
) {}