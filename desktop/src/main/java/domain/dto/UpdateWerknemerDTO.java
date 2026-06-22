package domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;

public record UpdateWerknemerDTO(
        @JsonIgnore int id,
        String naam,
        String voornaam,
        String email,
        String telefoonnummer,
        LocalDate geboortedatum,
        String rol,
        String status
) {
}
