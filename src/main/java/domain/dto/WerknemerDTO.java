package domain.dto;

import java.time.LocalDate;

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