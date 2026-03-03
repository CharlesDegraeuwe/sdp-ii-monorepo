package hogent.sdp2.backend.dto.request;

import java.time.LocalDate;

public record AfwezigheidAanmakenDTO(
        int werknemerId,
        LocalDate startDatum,
        LocalDate eindDatum,
        String reden,
        byte[] certificaat
) {}