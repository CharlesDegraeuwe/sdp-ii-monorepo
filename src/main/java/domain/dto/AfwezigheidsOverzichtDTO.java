package domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AfwezigheidsOverzichtDTO(
        Integer werknemerId,
        String voornaam,
        String naam,
        String type,
        LocalDate startDatum,
        LocalDate eindDatum,
        String status
) {}