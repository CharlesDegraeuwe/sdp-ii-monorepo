package hogent.sdp2.backend.rest.dto.response;

import java.time.LocalDate;

public record WerknemerResponseDTO(
        int id,
        String naam,
        String voornaam,
        String email,
        String telefoonnummer,
        LocalDate geboortedatum,
        String rol,
        String status) {}
