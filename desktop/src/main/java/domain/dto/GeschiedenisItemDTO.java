package domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeschiedenisItemDTO(
        Integer id,
        String type,
        LocalDate startDatum,
        LocalDate eindDatum,
        String status,
        String omschrijving
) {}