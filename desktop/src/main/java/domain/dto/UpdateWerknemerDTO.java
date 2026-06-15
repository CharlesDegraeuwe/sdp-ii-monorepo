package domain.dto;

import java.time.LocalDate;

public record UpdateWerknemerDTO(
        String naam,
        String voornaam,
        String email,
        String telefoonnummer,
        LocalDate geboortedatum
) {
}
