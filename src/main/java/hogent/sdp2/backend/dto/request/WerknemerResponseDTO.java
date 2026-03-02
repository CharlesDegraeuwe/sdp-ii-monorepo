package hogent.sdp2.backend.dto.request;

import java.time.LocalDate;

public record WerknemerResponseDTO(
        int id,
        String naam,
        String voornaam,
        String email,
        String telefoonnummer,
        LocalDate geboortedatum,
        String rol,
        String status
) {}