package hogent.sdp2.backend.rest.dto.auth;

import java.time.LocalDate;

public record AuthDTO(
        int id,
        String naam,
        String voornaam,
        String email,
        String telefoonnummer,
        LocalDate geboortedatum,
        String rol,
        String status
) {
}
