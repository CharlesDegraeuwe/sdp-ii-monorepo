package hogent.sdp2.backend.rest.dto.request;

import java.time.LocalDate;

// In hogent.sdp2.backend.rest.dto.request
public record TaakAanmakenDTO(
        int werknemerId,
        String titel,
        String beschrijving,
        LocalDate deadline,
        int siteId,
        String startuur,
        String einduur) {}
