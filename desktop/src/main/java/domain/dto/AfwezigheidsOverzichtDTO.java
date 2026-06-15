package domain.dto;

import java.time.LocalDate;

public record AfwezigheidsOverzichtDTO(
        Integer werknemerId,
        String voornaam,
        String naam,
        String type,
        LocalDate startDatum,
        LocalDate eindDatum,
        String status
) {}