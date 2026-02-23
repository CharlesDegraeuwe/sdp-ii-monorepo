package hogent.sdp2.backend.dto.request;

import java.time.LocalDate;

public record WerknemerAanmakenDTO(
        String naam,
        String voornaam,
        String email,
        String wachtwoord,
        String telefoonnummer,
        LocalDate geboortedatum,
        String rol,
        String status
) {}