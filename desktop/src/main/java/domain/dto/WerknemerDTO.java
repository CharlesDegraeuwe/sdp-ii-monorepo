package domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WerknemerDTO(
        int id,
        String naam,
        String voornaam,
        String email,
        String telefoonnummer,
        LocalDate geboortedatum,
        String rol,
        String status
) {}