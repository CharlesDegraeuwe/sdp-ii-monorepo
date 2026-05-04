package hogent.sdp2.backend.REST.dto.request;

import java.time.LocalDate;

public record UpdateUserDTO(
    String naam,
    String voornaam,
    String email,
    String telefoonnummer,
    LocalDate geboortedatum,
    String status
) {}