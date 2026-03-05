package hogent.sdp2.backend.dto.request;

import java.time.LocalDate;

public record UpdateUserDTO(
    String naam,
    String voornaam,
    String email,
    String telefoonnummer,
    LocalDate geboortedatum
) {}