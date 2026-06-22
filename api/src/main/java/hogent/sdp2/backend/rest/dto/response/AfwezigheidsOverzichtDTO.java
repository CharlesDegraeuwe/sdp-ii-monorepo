package hogent.sdp2.backend.rest.dto.response;

import java.time.LocalDate;

public record AfwezigheidsOverzichtDTO(
        Integer werknemerId,
        String voornaam,
        String naam,
        String type, // "Verlof" of "Ziekte"
        LocalDate startDatum,
        LocalDate eindDatum,
        String status // voor verlof: Goedgekeurd, In afwachting / voor ziekte: null
        ) {}
