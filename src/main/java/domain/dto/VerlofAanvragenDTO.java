package domain.dto;

import java.time.LocalDate;

public record VerlofAanvragenDTO(
        int werknemerId,
        LocalDate startDatum,
        LocalDate eindDatum,
        String type
) {}